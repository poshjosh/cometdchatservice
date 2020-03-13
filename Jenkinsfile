#!/usr/bin/env groovy
/**
 * https://github.com/poshjosh/cometdchatservice
 * @see https://hub.docker.com/_/maven
 * @see https://github.com/carlossg/docker-maven
 */
def ADDITIONAL_MAVEN_ARGS = ''
pipeline {
    agent any
    /**
     * parameters directive provides a list of parameters which a user should provide when triggering the Pipeline
     * some of the valid parameter types are booleanParam, choice, file, text, password, run, or string
     */
    parameters {
        string(name: 'SERVER_PORT', defaultValue: "8092", description: 'Server port')
        string(name: 'JAVA_OPTS'
                defaultValue: "-XX:+TieredCompilation -noverify -XX:TieredStopAtLevel=1 -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap",
                description: 'Java environment variables')
        string(name: 'CMD_LINE_ARGS',
                defaultValue: 'spring.jmx.enabled=false',
                description: 'Command line arguments')
        string(name: 'MAIN_CLASS', defaultValue: '', description: 'Java main class')
        choice(name: 'DEBUG', choices: ['Y', 'N'], description: 'Debug?')
    }
    environment {
        ARTIFACTID = readMavenPom().getArtifactId()
        VERSION = readMavenPom().getVersion()
        APP_ID = "${ARTIFACTID}:${VERSION}"
        IMAGE_REF = "poshjosh/${APP_ID}"
        IMAGE_NAME = IMAGE_REF.toLowerCase()
    }
    options {
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
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
        stage('Maven') {
            agent {
                docker {
                    image 'maven:3-alpine'
                    reuseNode 'true'
                    args "-u root -v /home/.m2:/root/.m2"
                }
            }
//            environment{
//                if(params.DEBUG == 'Y') {
//                    ADDITIONAL_MAVEN_ARGS = '-X'
//                }else{
//                    ADDITIONAL_MAVEN_ARGS = ''
//                } 
//            }
            stages{
                stage('Debug') {
                    when {
                        expression {
                            return DEBUG == 'Y'
                        }
                    }
                    steps {
                        sh 'printenv'
                    }
                }
                stage('Build Artifact') {
                    steps {
                        sh 'mvn -B ${ADDITIONAL_MAVEN_ARGS} clean compiler:compile'
                    }
                }
                stage('Unit Tests') {
                    steps {
                        sh 'mvn -B ${ADDITIONAL_MAVEN_ARGS} resources:testResources compiler:testCompile surefire:test'
                    }
                    post {
                        always {
                            junit(
                                allowEmptyResults: true,
                                testResults: '**/target/surefire-reports/TEST-*.xml'
                            )
                        }
                    }
                }
                stage('Quality Assurance') {
                    parallel {
                        stage('Integration Tests') {
                            steps {
                                sh 'mvn -B ${ADDITIONAL_MAVEN_ARGS} failsafe:integration-test failsafe:verify'
                            }
                        }
                        stage('Sanity Check') {
                            steps {
                                sh 'mvn -B ${ADDITIONAL_MAVEN_ARGS} checkstyle:checkstyle pmd:pmd pmd:cpd com.github.spotbugs:spotbugs-maven-plugin:spotbugs'
                            }
                        }
                        stage('Sonar Scan') {
                            environment {
                                SONAR = credentials('sonar-creds') // Must have been specified in Jenkins
                            }
                            steps {
                                // -Dsonar.host.url=${env.SONARQUBE_HOST}
                                sh "mvn -B ${ADDITIONAL_MAVEN_ARGS} sonar:sonar -Dsonar.login=$SONAR_USR -Dsonar.password=$SONAR_PSW"
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
                waitUntil {
                    try {
                        deleteDir() /* clean up workspace */
                    } catch(error) {
                        try{
                            deleteDir() /* clean up workspace */
                        }catch(error) {
                            return false;
                        }
                    }
                }
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
