package com.crossover.trial.journals.controller;

import com.crossover.trial.journals.Application;
import com.crossover.trial.journals.model.Journal;
import com.crossover.trial.journals.model.Publisher;
import com.crossover.trial.journals.repository.PublisherRepository;
import com.crossover.trial.journals.service.CurrentUser;
import com.crossover.trial.journals.service.JournalService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.Principal;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static com.crossover.trial.journals.javautil.FileIOs.createDirectoryIfNotExist;
import static java.io.File.*;

@Controller
public class PublisherController {

	private static final Logger log = Logger.getLogger(PublisherController.class);
	private static final String REDIRECT_PUBLISHER_PUBLISH = "redirect:/publisher/publish";

	@Autowired
	private PublisherRepository publisherRepository;

	@Autowired
	private JournalService journalService;

	@RequestMapping(method = RequestMethod.GET, value = "/publisher/publish")
	public String provideUploadInfo(Model model) {
		return "publisher/publish";
	}

	@RequestMapping(method = RequestMethod.POST, value = "/publisher/publish")
	@PreAuthorize("hasRole('PUBLISHER')")
	public String handleFileUpload(
			@RequestParam("name") String name,
			@RequestParam("category") Long categoryId,
			@RequestParam("file") MultipartFile multipartFile,
			RedirectAttributes redirectAttributes,
			@AuthenticationPrincipal Principal principal
	) {

		CurrentUser activeUser = (CurrentUser) ((Authentication) principal).getPrincipal();
		Optional<Publisher> publisherOptional = publisherRepository.findByUser(activeUser.getUser());

		return publisherOptional
			.map(processJournalPublication(name, categoryId, multipartFile, redirectAttributes))
			.orElseGet(() -> {
				redirectAttributes.addFlashAttribute(
					"message",
					"Publisher not found."
				);
				return REDIRECT_PUBLISHER_PUBLISH;
			});
	}

	private Function<Publisher, String> processJournalPublication(
			String name,
			Long categoryId,
			MultipartFile multipartFile,
			RedirectAttributes redirectAttributes
	) {
		return publisher -> {
            String uuid = UUID.randomUUID().toString();
            File f = fileLocationForPublisher(publisher, uuid);
            if (!multipartFile.isEmpty()) {
                try {
					try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(f))) {
						FileCopyUtils.copy(multipartFile.getInputStream(), stream);
					}
                    publishJournal(name, categoryId, publisher, uuid);
                    return "redirect:/publisher/browse";
                } catch (Exception e) {
                    log.info("Unable to process uploaded file", e);
                    redirectAttributes.addFlashAttribute(
						"message",
						"You failed to publish " + name + " => " + e.getMessage()
					);
                }
            } else {
                redirectAttributes.addFlashAttribute(
					"message",
					"You failed to upload " + name + " because the file was empty"
				);
            }

            return REDIRECT_PUBLISHER_PUBLISH;
        };
	}

	File fileLocationForPublisher(Publisher publisher, String fileUuid) {
		File dir = new File(getDirectory(publisher.getId()));
		createDirectoryIfNotExist(dir);
		return new File(getFileName(publisher.getId(), fileUuid));
	}

	void publishJournal(String name, Long categoryId, Publisher publisher, String uuid) {
		Journal journal = new Journal();
		journal.setUuid(uuid);
		journal.setName(name);
		journalService.publish(publisher, journal, categoryId);
	}

	public static String getFileName(long publisherId, String uuid) {
		return getDirectory(publisherId) + separator + uuid + ".pdf";
	}

	private static String getDirectory(long publisherId) {
		return Application.ROOT.get() + separator + publisherId;
	}

}