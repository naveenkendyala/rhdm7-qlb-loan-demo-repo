#!groovy

// Important
// Remember to ensure that the Project version information is on top of the pom.xml file because
// the getVersionFromPom will attempt to read the version information that it encounter at the
// first occurance.

node('maven') {

  def mvnCmd = "mvn -s openshift-nexus-settings.xml"
  // Need to use the public domain name instead of the internal service name, else server host not found error will appear. Suspect if I have multiple Nexus deployed into different projects on a share environment.
  def nexusReleaseURL = "http://nexus-demo-nexus.apps.cluster-8408.8408.sandbox712.opentlc.com/repository/maven-releases/"
  def mavenRepoURL = "http://nexus-demo-nexus.apps.cluster-8408.8408.sandbox712.opentlc.com/#browse/browse:maven-all-public/"
  def demoProjectName = "demo"
  def kieserver_keystore_password="mykeystorepass"
  def KIE_ADMIN_USER="admin"
  def KIE_ADMIN_PWD="admin"

  // ** NOTE: This 'M2' maven tool must be configured in the global configuration
  //def mvnHome = tool 'M2'
  
  stage('Checkout Source') {
    checkout scm
  }
 
  // In order to access to pom.xml, these variables and method calls must be placed after checkout scm.
  def groupId    = getGroupIdFromPom("pom.xml")
  def artifactId = getArtifactIdFromPom("pom.xml")
  def version    = getVersionFromPom("pom.xml")
  def packageName = getGeneratedPackageName(groupId, artifactId, version)
  
 stage('Clean Project') {
   sh "${mvnCmd} clean"
 }
  
    stage('Build KJar') {
    sh "${mvnCmd} package -DskipTests=true"
  }

  stage('Publish KJar to Nexus') {
    echo "Publish KJar file to Nexus..."
    // Remove the ::default from altDeploymentRepository due to the bugs reported at 
    // https://issues.apache.org/jira/browse/MDEPLOY-244?focusedCommentId=16648217&page=com.atlassian.jira.plugin.system.issuetabpanels%3Acomment-tabpanel#comment-16648217
    // https://support.sonatype.com/hc/en-us/articles/360010223594-maven-deploy-plugin-version-3-0-0-M1-deploy-fails-with-401-ReasonPhrase-Unauthorized
    // sh "${mvnCmd} deploy -DskipTests=true -DaltDeploymentRepository=nexus::default::${nexusReleaseURL}"

    sh "${mvnCmd} deploy -DskipTests=true -DaltDeploymentRepository=nexus::${nexusReleaseURL}"
    echo "Generated jar file: ${packageName}"
  }

  stage('Deploy Decision Service') {
    SH_SERVICE = sh (
      script: "oc get svc qlb-loan-demo-kieserver --no-headers=true --ignore-not-found=true -n ${demoProjectName}",
      returnStdout: true
    ).trim()
    if ("${SH_SERVICE}" == ""){
      SH_SECRET = sh (
        script: "oc get secret kieserver-app-secret --ignore-not-found=true -n ${demoProjectName}",
        returnStdout: true
      ).trim()
      if ("${SH_SECRET}" == ""){
        echo "keystore-app-secret not available. Creating now ..."
        echo "Generating keystore.jks ..."
        sh "keytool -genkeypair -alias jboss -keyalg RSA -keystore ./keystore.jks -storepass ${kieserver_keystore_password} --dname 'CN=demo1,OU=Demo,O=ocp.demo.com,L=KL,S=KL,C=MY'"
        echo "Creating kieserver-app-secret..."
        sh "oc create secret generic kieserver-app-secret --from-file=./keystore.jks -n ${demoProjectName}"
      }
      echo "Deploying Decision Server into OCP ..."
      sh "oc new-app -f ./templates/rhpam711-prod-immutable-kieserver.yaml -p KIE_SERVER_HTTPS_SECRET=kieserver-app-secret -p APPLICATION_NAME=qlb-loan-demo -p KIE_SERVER_HTTPS_PASSWORD=${kieserver_keystore_password} -p KIE_SERVER_CONTAINER_DEPLOYMENT=qlb-rules=com.redhat.demo.qlb:loan-pre-approval:${version} -p KIE_SERVER_MODE=DEVELOPMENT -p KIE_SERVER_MGMT_DISABLED=true -p KIE_SERVER_STARTUP_STRATEGY=LocalContainersStartupStrategy -p MAVEN_REPO_URL=${nexusReleaseURL} -p MAVEN_REPO_USERNAME=admin -p MAVEN_REPO_PASSWORD=admin123 -p KIE_ADMIN_USER=${KIE_ADMIN_USER} -p KIE_ADMIN_PWD=${KIE_ADMIN_PWD} -n ${demoProjectName}"
      //sh "oc new-app -f ./templates/rhdm73-kieserver.yaml -p KIE_SERVER_HTTPS_SECRET=kieserver-app-secret -p APPLICATION_NAME=travel-insurance-rules -p KIE_SERVER_HTTPS_PASSWORD=${kieserver_keystore_password} -p KIE_SERVER_CONTAINER_DEPLOYMENT=tinsurance-rules=com.myspace:insurance-rules-demo:1.0.0 -p KIE_SERVER_MODE=DEVELOPMENT -p KIE_SERVER_MGMT_DISABLED=true -p KIE_SERVER_STARTUP_STRATEGY=LocalContainersStartupStrategy -p MAVEN_REPO_URL=${nexusReleaseURL} -p MAVEN_REPO_USERNAME=admin -p MAVEN_REPO_PASSWORD=admin123 -n ${demoProjectName} -p MAVEN_MIRROR_URL=${mavenRepoURL}"
    }
    else{
      echo "Rollout POD to have the container to use the lastest build jar from nexus repo..."

      sh "oc rollout latest dc/qlb-loan-demo-kieserver -n ${demoProjectName}"
    }
  }

}

// Convenience Functions to read variables from the pom.xml
// Do not change anything below this line.
def getVersionFromPom(pom) {
  def matcher = readFile(pom) =~ '<version>(.+)</version>'
  matcher ? matcher[0][1] : null
}
def getGroupIdFromPom(pom) {
  def matcher = readFile(pom) =~ '<groupId>(.+)</groupId>'
  matcher ? matcher[0][1] : null
}
def getArtifactIdFromPom(pom) {
  def matcher = readFile(pom) =~ '<artifactId>(.+)</artifactId>'
  matcher ? matcher[0][1] : null
}

def getGeneratedPackageName(groupId, artifactId, version){
    String warFileName = "${groupId}.${artifactId}"
    warFileName = warFileName.replace('.', '/')
    "${warFileName}/${version}/${artifactId}-${version}.jar"
}
