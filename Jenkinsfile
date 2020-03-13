#!/usr/bin/env groovy
/**
 * https://github.com/poshjosh/cometdchatservice
 * @see https://hub.docker.com/_/maven
 */
pipeline {
    agent any
    environment {
        APP_PORT = '8092'
        ARTIFACTID = readMavenPom().getArtifactId();
        VERSION = readMavenPom().getVersion()
        PROJECT_NAME = "${ARTIFACTID}:${VERSION}"
        IMAGE_REF = "poshjosh/${PROJECT_NAME}";
        IMAGE_NAME = IMAGE_REF.toLowerCase()
//        RUN_ARGS = "--rm -v /home/.m2:/usr/share/maven/ref/repository /home/.m2:/root/.m2 -p ${APP_PORT}:${APP_PORT}"
//        RUN_ARGS = "--rm -v ${PWD}:/var/jenkins_home/workspace/cometdchatservice_dev -v /home/.m2:/root/.m2 -v ${PWD}/target:/var/jenkins_home/workspace/cometdchatservice_dev/target -p ${APP_PORT}:${APP_PORT}"
        RUN_ARGS = "-v ${PWD}:/usr/src/${ARTIFACTID} -v /home/.m2:/root/.m2 -v ${PWD}target:/usr/src/${ARTIFACTID}/target -w /usr/src/${ARTIFACTID} -p ${APP_PORT}:${APP_PORT}"
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
        stage('Build Image') {
            steps {
                script {
                    def additionalBuildArgs = "--pull"
                    if (env.BRANCH_NAME == "master") {
                        additionalBuildArgs = "--pull --no-cache"
                    }
                    docker.build("${IMAGE_NAME}", "${additionalBuildArgs} .")
                }
            }
        }
        stage('Build Artifact') {
            steps {
//                sh "docker run -d -u 1000:1000 --rm -v /:/var/jenkins_home/workspace/cometdchatservice_dev -v /home/.m2:/root/.m2 -v //target:/var/jenkins_home/workspace/cometdchatservice_dev/target -p 8092:8092 -w /var/jenkins_home/workspace/cometdchatservice_dev -v /var/jenkins_home/workspace/cometdchatservice_dev:/var/jenkins_home/workspace/cometdchatservice_dev:rw,z -v /var/jenkins_home/workspace/cometdchatservice_dev@tmp:/var/jenkins_home/workspace/cometdchatservice_dev@tmp:rw,z"
//                sh "docker run -d -u 1000:1000 --rm -v ${PWD}:/var/jenkins_home/workspace/cometdchatservice_dev:rw,z -v ${PWD}@tmp:/var/jenkins_home/workspace/cometdchatservice_dev@tmp:rw,z -v /home/.m2:/root/.m2 -v ${PWD}/target:/var/jenkins_home/workspace/cometdchatservice_dev/target -p ${APP_PORT}:${APP_PORT} -w /var/jenkins_home/workspace/cometdchatservice_dev ${IMAGE_NAME}"
                echo "HOME = ${HOME}"
                echo "PWD = ${PWD}"
//                sh '''
//                    docker run -d -u 1000:1000 --rm --name "${ARTIFACTID}" -v "${PWD}":/usr/src/"${ARTIFACTID}" -v /home/.m2:/root/.m2 -v "${PWD}"target:/usr/src/"${ARTIFACTID}"/target -w /usr/src/"${ARTIFACTID}" -p "${APP_PORT}:${APP_PORT}" "${IMAGE_NAME}"
//                    docker exec -d -u 1000 "${ARTIFACTID}" /bin/bash
//                    mvn -X -B clean compiler:compile
//                '''
                script{
                    docker.image("${IMAGE_NAME}").inside("${RUN_ARGS}"){
                        sh 'mvn -X -B clean compiler:compile'
                    }
                }
            }
        }
        stage('Unit Tests') {
            steps {
                script{
                    docker.image("${IMAGE_NAME}").inside{
                        sh 'mvn -B resources:testResources compiler:testCompile surefire:test'
                    }
                }
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
        stage('Quality Analysis') {
            parallel {
                stage('Integration Tests') {
                    steps {
                        script{
                            docker.image("${IMAGE_NAME}").inside{
                                sh 'mvn -B failsafe:integration-test failsafe:verify'
                            }
                        }
                    }
                }
                stage('Sanity Check') {
                    steps {
                        script{
                            docker.image("${IMAGE_NAME}").inside{
                                sh 'mvn -B checkstyle:checkstyle pmd:pmd pmd:cpd com.github.spotbugs:spotbugs-maven-plugin:spotbugs'
                            }
                        }
                    }
                }
                stage('Sonar Scan') {
                    environment {
                        SONAR = credentials('sonar-creds') // Must have been specified in Jenkins
                    }
                    steps {
                        script{
                            // -Dsonar.host.url=${env.SONARQUBE_HOST}
                            docker.image("${IMAGE_NAME}").inside{
                                sh "mvn -B sonar:sonar -Dsonar.login=$SONAR_USR -Dsonar.password=$SONAR_PSW"
                            }
                        }
                    }
                }
            }
        }
        stage('Documentation') {
            steps {
                script{
                    docker.image("${IMAGE_NAME}").inside{
                        sh 'mvn -B site:site'
                    }
                }
            }
            post {
                always {
                    publishHTML(target: [reportName: 'Site', reportDir: 'target/site', reportFiles: 'index.html', keepAll: false])
                }
            }
        }
        stage('Run Image') {
            steps {
                script{
                    docker.image("${IMAGE_NAME}").run()
                }
            }
        }
        stage('Install Local') {
            steps {
                script{
                    docker.image("${IMAGE_NAME}").inside{
                        sh 'mvn -B jar:jar source:jar install:install'
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
    post {
        always {
            deleteDir() /* clean up workspace */
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
