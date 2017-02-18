const R = require('ramda');
const AWS = require('aws-sdk');

const ses = new AWS.SES();
const sqs = new AWS.SQS({region:'us-west-2'});


const normalizeJsonMsg = jsonMsg => {
  const originalJsonMsg = Object.assign({}, jsonMsg);
  return jsonMsg.subscriptions.map(subscription => Object.assign({}, originalJsonMsg, {subscription: subscription}));
};

const extractJSONDataFromSNSMessage = R.pipe(
  R.prop('Records'),
  R.head,
  R.prop('Sns'),
  R.prop('Message'),
  JSON.parse
);

const generateTitle = journalJson => {
  const journalName = journalJson.name;
  const categoryName = journalJson.category.name;
  return `New Journal "${journalName} added for "${categoryName}" Category`;
};

const generateEmailBody = journalJson => {
  const journalName = journalJson.name;
  const categoryName = journalJson.category.name;
  const loginName = journalJson.subscription.user.loginName;
  const viewUrl = `http://localhost:8080/view/${journalJson.id}`;
  const publicationName = journalJson.publisher.name;
  const publisherName = journalJson.publisher.user.loginName;

  return `
    <h1>New Journal "${journalName}" added for "${categoryName}" Category</h1>
    
    <h2>Hi <strong>${loginName}!</strong></h2>
    
    <p>
      A new journal has been recently published on <strong>"${publicationName}"</strong> by <strong>${publisherName}</strong>
      You can view it at <a href="${viewUrl}">${viewUrl}</a>.    
    </p>
  `;
};

module.exports = (event, context, callback) => {

  const unnormalizedJournalJsons = extractJSONDataFromSNSMessage(event);

  const getAwsAccountId = () => iam.getUser().promise().then(data => {
    const arn = data.User.Arn;
    const regex = /^arn:aws:iam:([A-Za-z0-9-]*):([0-9]*):user/;
    return regex.exec(arn)[2];
  });

  const sqsPromises = unnormalizedJournalJsons => {
      const sqsParams = {
        MessageBody: JSON.stringify(unnormalizedJournalJsons),
        QueueUrl: 'https://sqs.us-west-2.amazonaws.com/891170581541/journals-faiz-allsubscriptions'
      };
      console.log(`[DEBUG] Sending message to SQS: ${sqsParams.QueueUrl}`);
      return sqs.sendMessage(sqsParams).promise().then(() => unnormalizedJournalJsons);
  };

  const emailPromises = (journalJsons) => journalJsons.map(journalJson => {
    const toAddress = journalJson.publisher.user.email;
    const params = {
      Destination: {
        ToAddresses: [toAddress]
      },
      Message: {

        Subject: {
          Data: generateTitle(journalJson),
          Charset: 'UTF-8'
        },

        Body: {
          Html: {
            Data: generateEmailBody(journalJson),
            Charset: 'UTF-8'
          }
        }
      },
      Source: 'faizhasim@gmail.com',
      ReplyToAddresses: [
        'no-reply@whatever.com <faizhasim@gmail.com>'
      ]
    };
    console.log('[DEBUG] ', JSON.stringify(journalJson));

    return ses.sendEmail(params).promise().then(() => journalJson);
  });

  return sqsPromises(unnormalizedJournalJsons)
    .then(normalizeJsonMsg)
    .then(emailPromises)
    .then(promises => {
      console.log(`[DEBUG] promises = `, promises);
      try {
        Promise.all(promises).then(() => {});
      } catch (err) {
        console.log('whatever: ', err);
      }
      return callback();
    })
    .catch(err => {
        console.log('Error: ', err, err.stack);
      callback(err);
      throw err;
    });
};


