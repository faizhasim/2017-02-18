const R = require('ramda');
const AWS = require('aws-sdk');

const ses = new AWS.SES();

const normalizeJsonMsg = jsonMsg => {
  const originalJsonMsg = Object.assign({}, jsonMsg);
  return jsonMsg.subscriptions.map(subscription => Object.assign({}, originalJsonMsg, {subscription: subscription}));
};

const extractJSONDataFromSNSMessage = R.pipe(
  R.prop('Records'),
  R.head,
  R.prop('Sns'),
  R.prop('Message'),
  JSON.parse,
  normalizeJsonMsg
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

  const journalJsons = extractJSONDataFromSNSMessage(event);

  const emailPromises = journalJsons.map(journalJson => new Promise((resolve, reject) => {
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

      ses.sendEmail(params, (err, data) => {
        if (err) {
          console.log('Failed to send email! ', err, err.stack);
          return reject(err);
        }
        return resolve(data);
    });
  }));

  return Promise.all(emailPromises)
    .then(() => callback())
    .catch(err => {
      console.log('Error: ', err, err.stack);
      callback(err);
      throw err;
    });
};


