#!/usr/bin/env groovy
/**
 * https://github.com/poshjosh/cometdchatservice
 * @see https://hub.docker.com/_/maven
 * @see https://github.com/carlossg/docker-maven
 */
pipeline {
    agent any
    /**
     * parameters directive provides a list of parameters which a user should provide when triggering the Pipeline
     * some of the valid parameter types are booleanParam, choice, file, text, password, run, or string
     */
    parameters {
        string(name: 'ORG_NAME', defaultValue: "poshjosh", 
                description: 'Name of the organization. (Docker Hub/GitHub)')
        string(name: 'SERVER_PROTOCOL', defaultValue: "http", 
                description: 'Server protocol, e.g http, https etc')
        string(name: 'SERVER_BASE_URL', defaultValue: "http://localhost", 
                description: 'Server protocol://host, without the port')
        string(name: 'SERVER_PORT', defaultValue: "8092", description: 'Server port')
        string(name: 'SERVER_CONTEXT', defaultValue: "/", 
                description: 'Server context path. Must begin with a forward slash / ')
        string(name: 'JAVA_OPTS',
                defaultValue: "-XX:+TieredCompilation -noverify -XX:TieredStopAtLevel=1 -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap",
                description: 'Java environment variables')
        string(name: 'CMD_LINE_ARGS', 
                defaultValue: 'spring.jmx.enabled=false',
                description: 'Command line arguments')
        string(name: 'MAIN_CLASS', 
                defaultValue: 'com.looseboxes.cometd.chatservice.CometDApplication', 
                description: 'Java main class')
        string(name: 'SONAR_BASE_URL', defaultValue: 'http://localhost',
                description: 'Sonarqube base URL. Will be combined with port to build Sonarqube property sonar.host.url')    
        string(name: 'SONAR_PORT', defaultValue: '9000',
                description: 'Port for Sonarqube server')    
        string(name: 'TIMEOUT', defaultValue: '45', 
                description: 'Max time that could be spent in MINUTES')
        choice(name: 'DEBUG', choices: ['Y', 'N'], description: 'Debug?')
    }
    environment {
        ARTIFACTID = readMavenPom().getArtifactId()
        VERSION = readMavenPom().getVersion()
        APP_ID = "${ARTIFACTID}:${VERSION}"
        IMAGE_REF = "${ORG_NAME}/${APP_ID}"
        IMAGE_NAME = IMAGE_REF.toLowerCase()
        SERVER_URL = "${params.SERVER_BASE_URL}:${params.SERVER_PORT}${params.SERVER_CONTEXT}"
        ADDITIONAL_MAVEN_ARGS = "${params.DEBUG == 'Y' ? '-X' : ''}"
    }
    options {
        skipDefaultCheckout true // We are thus able to call checkout scm at our convenience
        timestamps()
        timeout(time: "${params.TIMEOUT}", unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '4'))
        skipStagesAfterUnstable()
        disableConcurrentBuilds()
    }
    triggers {
// @TODO use webhooks from GitHub
// Once in every 2 hours slot between 0900 and 1600 every Monday - Friday
        pollSCM('H H(8-16)/2 * * 1-5')
    }
    stages {
//                stage('Build Artifact') {
//                    steps {
//                        script {
//                            if(params.DEBUG == 'Y') {
//                                sh 'printenv'
//                            }
//                        }
//                        sh 'mvn -B ${ADDITIONAL_MAVEN_ARGS} clean compiler:compile'
//                    }
//                }
//                stage('Unit Tests') {
//                    steps {
//                        sh 'mvn -B ${ADDITIONAL_MAVEN_ARGS} resources:testResources compiler:testCompile surefire:test'
//                        jacoco execPattern: 'target/jacoco.exec'    
//                    }
//                    post {
//                        always {
//                            junit(
//                                allowEmptyResults: true,
//                                testResults: '**/target/surefire-reports/TEST-*.xml'
//                            )
//                        }
//                    }
//                }
        stage('Package') {
            agent {
                docker {
                    image 'maven:3-alpine'
                    args '-u root -v /home/.m2:/root/.m2'
                }
            }
            steps {
                echo '- - - - - - - PACKAGE - - - - - - -'
                sh 'ls -a && cd .. && ls -a && cd .. && ls -a'
                sh 'mvn -B ${ADDITIONAL_MAVEN_ARGS} jar:jar'
                sh "mkdir ${env.WORKSPACE}/mytarget && cp -r /target ${env.WORKSPACE}/mytarget"
            }
            post {
                always {
                    archiveArtifacts artifacts: 'target/*.jar', onlyIfSuccessful: true
                }
            }
        }
        stage('Build Image') {
            steps {
                checkout scm
                script {
                    echo '- - - - - - - BUILD IMAGE - - - - - - -'
                    sh 'ls -a && cd .. && ls -a && cd .. && ls -a'
                    sh "cp -r ${env.WORKSPACE}/mytarget /target"
                    sh 'mkdir target/dependency'
                    sh 'cd target/dependency'
                    sh 'jar -xf ../*.jar'
                    def additionalBuildArgs = "--pull"
                    if (env.BRANCH_NAME == "master") {
                        additionalBuildArgs = "--pull --no-cache"
                    }
                    docker.build("${IMAGE_NAME}", "${additionalBuildArgs} .")
                }
            }
        }
        stage('Run Image') {
            steps {
                script{
                    echo '- - - - - - - RUN IMAGE - - - - - - -'
                    sh 'ls -a && cd .. && ls -a && cd .. && ls -a'
//                    def ARGS_MNT = "-v /home/.m2:/root/.m2"
//                    def ARGS_EXP = "--expose 9092 --expose ${params.SONAR_PORT}"
//                    def NO_PORT = (params.SERVER_PORT == '' || params.SERVER_PORT == null)
//                    def ARGS_OPTS
//                    if(NO_PORT) {
//                        ARGS_OPTS = "JAVA_OPTS=${params.JAVA_OPTS} MAIN_CLASS=${params.MAIN_CLASS} ${params.CMD_LINE_ARGS}"
//                    }else{
//                        ARGS_OPTS = "--server.port=${params.SERVER_PORT} SERVER_PORT=${params.SERVER_PORT} JAVA_OPTS=${params.JAVA_OPTS} MAIN_CLASS=${params.MAIN_CLASS} ${params.CMD_LINE_ARGS}"
//                    }    
//                    def RUN_ARGS = "-u root ${ARGS_MNT} ${ARGS_EXP} ${ARGS_OPTS}"

//                    if(NO_PORT) {
//                        docker.image("${IMAGE_NAME}").inside("${RUN_ARGS}") {
//                            sh 'java -version'
//                        }
//                    }else{
//                        docker.image("${IMAGE_NAME}")
//                            .inside("-p ${params.SERVER_PORT}:${params.SERVER_PORT}", "${RUN_ARGS}") {
                        docker.image("${IMAGE_NAME}")
                            .inside("-p 8092:8092", "--server.port=8092 -v /home/.m2:/root/.m2 --expose 9092 --expose 9090 MAIN_CLASS=com.looseboxes.cometd.chatservice.CometDApplication") {
                                echo '- - - - - - - INSIDE IMAGE - - - - - - -'
                        }
//                    }    
                }
            }
        }
        stage('Quality Assurance') {
            parallel {
                stage('Integration Tests') {
                    agent {
                        docker {
                            image 'maven:3-alpine'
                            args '-u root -v /home/.m2:/root/.m2'
                        }
                    }
                    steps {
// Step to ensure that the application under test is completely up and running.
// Simply waiting for the Docker container to be up is not enough as apps
// require a few seconds to initialize after the container is up.
//                        sh "curl --retry 5 --retry-connrefused --connect-timeout 5 --max-time 5 ${SERVER_URL}"
                        sh 'mvn -B ${ADDITIONAL_MAVEN_ARGS} failsafe:integration-test failsafe:verify'
                        jacoco execPattern: 'target/jacoco-it.exec'    
                    }
                    post {
                        always {
                            junit(
                                allowEmptyResults: true,
                                testResults: '**/target/failsafe-reports/TEST-*.xml'
                            )
                        }
                    }
                }
//                        stage('Sanity Check') {
//                            steps {
//                                sh 'mvn -B ${ADDITIONAL_MAVEN_ARGS} checkstyle:checkstyle pmd:pmd pmd:cpd com.github.spotbugs:spotbugs-maven-plugin:spotbugs'
//                            }
//                        }
                stage('Sonar Scan') {
                    agent {
                        docker {
                            image 'maven:3-alpine'
                            args '-u root -v /home/.m2:/root/.m2'
                        }
                    }
                    environment {
                        SONAR = credentials('sonar-creds') // Must have been specified in Jenkins
                        SONAR_URL = "${params.SONAR_BASE_URL}:${params.SONAR_PORT}"
                    }
                    steps {
                        sh "mvn -B ${ADDITIONAL_MAVEN_ARGS} sonar:sonar -Dsonar.login=$SONAR_USR -Dsonar.password=$SONAR_PSW -Dsonar.host.url=${SONAR_URL}"
                    }
                }
                stage('Documentation') {
                    agent {
                        docker {
                            image 'maven:3-alpine'
                            args '-u root -v /home/.m2:/root/.m2'
                        }
                    }
                    steps {
                        sh 'mvn -B site:site'
                    }
                    post {
                        always {
                            publishHTML(target: [reportName: 'Site', reportDir: 'target/site', reportFiles: 'index.html', keepAll: false])
                        }
                    }
                }
            }
        }
        stage('Install Local') {
            agent {
                docker {
                    image 'maven:3-alpine'
                    args '-u root -v /home/.m2:/root/.m2'
                }
            }
            steps {
                sh 'mvn -B ${ADDITIONAL_MAVEN_ARGS} source:jar install:install'
            }
        }
        stage('Deploy Image') {
            when {
                branch 'master'
            }
            steps {
                script {
                    docker.withRegistry('', 'dockerhub-creds') { // Must have been specified in Jenkins
                        sh "docker push ${IMAGE_NAME}"
                    }
                }
            }
        }
    }
    post {
        always {
            script{
                retry(3) {
                    try {
                        timeout(time: 60, unit: 'SECONDS') {
                            deleteDir() // Clean up workspace
                        } 
                    } catch (org.jenkinsci.plugins.workflow.steps.FlowInterruptedException e) {
                        // we re-throw as a different error, that would not 
                        // cause retry() to fail (workaround for issue JENKINS-51454)
                        error 'Timeout!'
                    } 
                } // retry ends
            }
            sh "docker system prune -f --volumes"
        }
        failure {
            mail(
                to: 'posh.bc@gmail.com',
                subject: "$IMAGE_NAME - Build # $BUILD_NUMBER - FAILED!",
                body: "$IMAGE_NAME - Build # $BUILD_NUMBER - FAILED:\n\nCheck console output at ${env.BUILD_URL} to view the results."
            )
        }
    }
}
