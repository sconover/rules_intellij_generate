// From the directory this file is in, run
//
// gcloud beta functions deploy subscribe --stage-bucket rules-intellij-generate-cloud-functions --trigger-topic cloud-builds


// subscribe is the main function called by Cloud Functions.
module.exports.subscribe = (event, callback) => {

  console.log("GCB -> GITHUB");
  // console.log(event.data.data); // in b64
  const build = eventToBuild(event.data.data); // decode
  console.log(build);

  // gcb...
  // sample of logging "build"...
  //
  // { id: 'd753e468-f95b-45cc-acae-091479616951',
  //   projectId: 'rules-intellij-generate',
  //   status: 'WORKING',
  //   source: {
  //     repoSource: {
  //       projectId: 'rules-intellij-generate',
  //       repoName: 'github-sconover-rules_intellij_generate',
  //       branchName: 'sconover.gcb_initial'
  //     }
  //   },
  //     steps: [
  //       { name: 'gcr.io/cloud-builders/gsutil', args: [Object], volumes: [Object] },
  //       { name: 'debian', args: [Object], volumes: [Object] },
  //       { name: 'gcr.io/cloud-builders/bazel', args: [Object], dir: 'rules', volumes: [Object] },
  //       { name: 'debian', args: [Object], volumes: [Object] },
  //       { name: 'gcr.io/cloud-builders/gsutil', args: [Object], volumes: [Object] },
  //       { name: 'gcr.io/cloud-builders/gsutil', args: [Object], volumes: [Object] },
  //       { name: 'debian', args: [Object], volumes: [Object] },
  //       { name: 'gcr.io/cloud-builders/bazel', args: [Object], dir: 'scenarios', volumes: [Object] },
  //       { name: 'debian', args: [Object], volumes: [Object] },
  //       { name: 'gcr.io/cloud-builders/gsutil', args: [Object], volumes: [Object] } ],
  //     createTime: '2017-11-11T22:09:32.950648110Z',
  //     startTime: '2017-11-11T22:09:33.651313965Z',
  //     timeout: '1800.000s',
  //     logsBucket: 'gs://535156622818.cloudbuild-logs.googleusercontent.com',
  //     sourceProvenance: {
  //       resolvedRepoSource: {
  //         projectId: 'rules-intellij-generate',
  //         repoName: 'github-sconover-rules_intellij_generate',
  //         commitSha: '0e249dc46149a79872ac221f802431b2a2765dc1'
  //       }
  //     },
  //     buildTriggerId: '742b0ca6-69fb-46a7-8f35-2c851ff9bb66',
  //     logUrl: 'https://console.cloud.google.com/gcr/builds/d753e468-f95b-45cc-acae-091479616951?project=rules-intellij-generate'
  //     }

  var gcbStatus = build['status']
  var startTime = build['startTime']
  var commitSha = build['sourceProvenance']['resolvedRepoSource']['commitSha'];
  var buildUrl = "https://console.cloud.google.com/gcr/builds/" + build['id'] + "?project=grpccraft";
  var logUrl = build['logUrl'];

  console.log(gcbStatus, startTime, commitSha, buildUrl, logUrl);

  // github...
  // I elected to generate a personal access token via: https://github.com/settings/tokens ,
  // with only repo:status scope.
  // This is an example of setting a sha to have a green checkmark in github:
  //
  //  curl -H 'Authorization: token <my-personal-access-token>' -X POST https://api.github.com/repos/sconover/rules_intellij_generate/statuses/d79a25a7f6740f2890bdc362cd3ca3f8cd6f61b7 -d '{"state": "success", "description": "Test 2 from Steve"}'

  // gcb statuses:
  // https://cloud.google.com/container-builder/docs/api/reference/rest/v1/projects.builds#status
  // STATUS_UNKNOWN	Status of the build is unknown.
  // QUEUED	Build is queued; work has not yet begun.
  // WORKING	Build is being executed.
  // SUCCESS	Build finished successfully.
  // FAILURE	Build failed to complete successfully.
  // INTERNAL_ERROR	Build failed due to an internal cause.
  // TIMEOUT	Build took longer than was allowed.
  // CANCELLED	Build was canceled by a user.

  // github status payload:
  // https://developer.github.com/v3/repos/statuses/
  // Parameters
  // Name	Type	Description
  //
  // state	string	Required. The state of the status. Can be one of
  //   error,
  //   failure,
  //   pending, or
  //   success.
  //
  // target_url	string	The target URL to associate with this status. This URL will be linked from the GitHub UI to allow users to easily see the source of the status.
  // For example, if your continuous integration system is posting build status, you would want to provide the deep link for the build output for this specific SHA:
  // http://ci.example.com/user/repo/build/sha
  //
  // description	string	A short description of the status.
  //
  // context	string	A string label to differentiate this status from the status of other systems. Default: default

  var gcbStateToGithubState = {
    'STATUS_UNKNOWN': 'pending',
    'QUEUED': 'pending',
    'WORKING': 'pending',
    'SUCCESS': 'success',
    'FAILURE': 'failure',
    'INTERNAL_ERROR': 'error',
    'TIMEOUT': 'error',
    'CANCELLED': 'error'
  }

  var githubState = gcbStateToGithubState[gcbStatus];
  var description = "[GCB] start=" + startTime;

  var request = require('request');
  request.post({
    headers: {
      'Authorization' : 'token your-personal-access-token-goes-here',
      'User-Agent': 'sconover-User-Agent'}, // github requires a user agent
    url:     'https://api.github.com/repos/copypastel/grpc-craft/statuses/' + commitSha,
    body:    '{"state": "' + githubState + '", "target_url": "' + logUrl + '", "description": "[GCB] ' + description + '"}'
  }, function(error, response, body){
    console.log(body);
  });
};

// eventToBuild transforms pubsub event message to a build object.
const eventToBuild = (data) => {
  return JSON.parse(new Buffer(data, 'base64').toString());
}
