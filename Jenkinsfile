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
//        javaOpts : '-Dserver.port=8092 -XX:+TieredCompilation -XX:TieredStopAtLevel=1 -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -noverify',

completePipeline(
        appPort : '8092',
        appEndpoint : '/actuator/health',
        javaOpts : '-XX:TieredStopAtLevel=1',
        mainClass : 'com.looseboxes.cometd.chatservice.CometDApplication'
)
