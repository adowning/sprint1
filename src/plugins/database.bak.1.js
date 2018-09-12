/*global $ */

//$( document ).ready(function() {

// --------------------
// - General Setup -
// --------------------

// - Database Setup (Variables come from config File)-
var cloudant_url = 'https://localhost:32778/tketl_posts'; // Set the general Cloudant URL
//TODO: var cloudant_auth = btoa(user + ':' + pass); // Creates a Base64 String of the User and Pass

// - Create a new local PouchDB -
var test = "hello";
var localDB = new PouchDB('todos');
var remoteDB = new PouchDB(cloudant_url, {
  ajax: {
    cache: false,
    timeout: 10000,
    /*headers: {
      'Authorization': 'Basic '+ cloudant_auth
    },*/
  },
});

// - Listen to Changes Feed -
localDB.changes({ // If something changes on the local db, call the changes() Function
  since: 'now',
  live: true
}).on('change', function (change) {
  console.log("CHANGES!");
  //changesChanging(change);
}).on('error', function (err) {
  changesError(err);
});

// - Receive and print general DB information -
localDB.info().then(function (info) {
  console.log(info);
});

// - Setup the replication/sync -
// may need a cache layer for text regions and real sync on official edits
var syncHandler = localDB.sync(remoteDB, {
  live: true, // Get the newest data live
  retry: true // Retry if connection was lost need to know how often this is called
}).on('change', function (change) { // If changes occur
  showStatus('good', 'Changes found');
}).on('complete', function (info) { // Is triggered by the cancel() function
  // replication was canceled!
}).on('error', function (err) { // If an unexpected error occurs
  showStatus('bad', 'Uff! Unexpected error occured!');
  $.JSONView(info, $('#output-sync'));
  console.log('ERROR!');
  console.log(error);
});

//});