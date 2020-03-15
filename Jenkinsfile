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
        string(name: 'SERVER_HOST', defaultValue: "localhost", 
                description: 'Server host, e.g ip address without the port')
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
        string(name: 'SONAR_URL', defaultValue: 'http://localhost:9000',
                description: 'Value for Sonarqube property sonar.host.url')    
        choice(name: 'DEBUG', choices: ['N', 'Y'], description: 'Debug - No or Yes?')
    }
    environment {
        ARTIFACTID = readMavenPom().getArtifactId()
        VERSION = readMavenPom().getVersion()
        APP_ID = "${ARTIFACTID}:${VERSION}"
        IMAGE_REF = "${ORG_NAME}/${APP_ID}"
        IMAGE_NAME = IMAGE_REF.toLowerCase()
        SERVER_URL = "${params.SERVER_PROTOCOL}://${params.SERVER_HOST}:${params.SERVER_PORT}${params.SERVER_CONTEXT}"
    }
    options {
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '4'))
//        skipStagesAfterUnstable()
        disableConcurrentBuilds()
    }
    triggers {
// @TODO use webhooks from GitHub
// Once in every 2 hours slot between 0900 and 1600 every Monday - Friday
        pollSCM('H H(8-16)/2 * * 1-5')
    }
    stages {
        stage('Mavenize') {
            agent {
                docker {
                    image 'maven:3-alpine'
                    args "--network jenkins -u root -v /home/.m2:/root/.m2 --expose 9090 --expose 9092"
                }
            }
            environment{
                ADDITIONAL_MAVEN_ARGS = "${params.DEBUG == 'Y' ? '-X' : ''}"
            }
            stages{
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
                stage('Quality Assurance') {
                    parallel {
                        stage('Integration Tests') {
                            steps {
// Step to ensure that the application under test is completely up and running.
// Simply waiting for the Docker container to be up is not enough as apps
// require a few seconds to initialize after the container is up.
                                sh "curl --retry 5 --retry-connrefused --connect-timeout 5 --max-time 5 ${SERVER_URL}"
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
                            environment {
                                SONAR = credentials('sonar-creds') // Must have been specified in Jenkins
                            }
                            steps {
                                sh "mvn -B ${ADDITIONAL_MAVEN_ARGS} sonar:sonar -Dsonar.login=$SONAR_USR -Dsonar.password=$SONAR_PSW -Dsonar.host.url=${params.SONAR_URL}"
                            }
                        }
                    }

                }
                stage('Documentation') {
                    steps {
                        sh 'mvn -B site:site'
                    }
                    post {
                        always {
                            publishHTML(target: [reportName: 'Site', reportDir: 'target/site', reportFiles: 'index.html', keepAll: false])
                        }
                    }
                }
                stage('Install Local') {
                    steps {
                        sh 'mvn -B ${ADDITIONAL_MAVEN_ARGS} jar:jar source:jar install:install'
                    }
                    post {
                        always {
                            archiveArtifacts artifacts: 'target/*.jar', onlyIfSuccessful: true
                        }
                    }
                }
            }
        }
        stage('Dockerize') {
            stages{
                stage('Build Image') {
                    steps {
                        sh '''
                            mkdir target/dependency
                            (cd target/dependency; jar -xf ../*.jar)
                        '''
                        script {
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
                            if(params.SERVER_PORT == '' || params.SERVER_PORT == null) {
                                docker.image("${IMAGE_NAME}")
                                    .withRun("JAVA_OPTS=${params.JAVA_OPTS} MAIN_CLASS=${params.MAIN_CLASS} ${params.CMD_LINE_ARGS}")
                            }else{
                                docker.image("${IMAGE_NAME}")
                                    .withRun('-p ${params.SERVER_PORT}:${params.SERVER_PORT}', 
                                    "--server.port=${params.SERVER_PORT} SERVER_PORT=${params.SERVER_PORT} JAVA_OPTS=${params.JAVA_OPTS} MAIN_CLASS=${params.MAIN_CLASS} ${params.CMD_LINE_ARGS}")
                            }    
                        }
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
        }
    }
    post {
        always {
            script{
                retry(3) {
                    try {
                        timeout(time: 5, unit: 'SECONDS') {
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
