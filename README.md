# Journals

> Medical Journals App

## Getting Started

- Configure `spring.datasource` to connect to MySQL instance in `application.properties`.

- Configure AWS configuration for publishing messages to SNS Topic in  `application.properties`.

      application.aws.access-key-id=xxxxx
      application.aws.secret-access-key=xxxx
      application.aws.region=us-west-2
      application.aws.journal-changes-arn=arn:aws:sns:us-west-2:xxxxx:journals-faiz-journalpublication
