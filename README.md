# Journals

> Medical Journals App


## Requirements

- AWS
- Node 7 & NPM
- Java 8 & Maven 3.3.x

## Getting Started

1. Configure AWS stack:

      npm install -g serverless && cd serverless/ && npm install && sls deploy
      
2. Take note of the `journalpublication` SNS Topic ARN, you will need it later to configure the Spring Boot app. 
  To list down all SNS Topic ARN in your account, type the following:
      
      aws sns list-topics
      
  And you will get similar output:
  
      {
          "Topics": [
              {
                  "TopicArn": "arn:aws:sns:us-west-2:XXXXXX:journals-faiz-journalpublication"
              }
          ]
      }

3. Configure `spring.datasource` to connect to MySQL instance in `application.properties`.

4. Configure AWS configuration for publishing messages to SNS Topic in  `application.properties`.

      application.aws.access-key-id=xxxxx
      application.aws.secret-access-key=xxxx
      application.aws.region=us-west-2
      application.aws.journal-changes-arn=arn:aws:sns:us-west-2:xxxxx:journals-faiz-journalpublication

5. Run Spring Boot app, optionally by configuring the upload dir:

      mvn spring-boot:run -Dupload-dir="/path/to/pdf/dir"