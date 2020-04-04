#!/usr/bin/env groovy
/**
 * https://github.com/poshjosh/cometdchatservice
 */
library(
    identifier: 'jenkins-shared-library@master',
    retriever: modernSCM(
        [
            $class: 'GitSCMSource',
            remote: 'https://github.com/poshjosh/jenkins-shared-library.git'
        ]
    )
)

pipelineForJavaSpringBoot(
        appPort : '8092',
        appEndpoint : '/actuator/health',
        mainClass : 'com.looseboxes.cometd.chatservice.CometDApplication'
)
