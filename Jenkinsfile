#!/usr/bin/env groovy
/**
 * https://github.com/poshjosh/cometdchatservice
 * @see https://hub.docker.com/_/maven
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

completePipeline(
        appEndpoint : '/actuator/health'
        mainClass : 'com.looseboxes.cometd.chatservice.CometDApplication',
        appPort : '8092'
)
