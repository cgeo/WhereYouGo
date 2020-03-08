WhereYouGo
==========

## About

This is a clone of this [WhereYouGo repository](https://github.com/biylda/WhereYouGo/) as it was found unmaintained and was (after confirmation of original author) transfered to here. 
Development can be continued seamless as we got the ability and allowance to publish the app under the original accounts and signing keys.

The  README.md of the old repository is attached at the end of this file for your information.

## Contact
As there are no specific support and contact channels for this project yet, please use the issue tracker of this repository to get in contact with the development community for the time being.

If PM contact is needed please contact us at support@cgeo.org 

## Development

### Build
to be completed

### Branches
- **master** is for development of new features.
- **release** is for all bug fixes of already existing features. So if a bug is reported in released version, it should be fixed on this branch (and merged to master afterwards).

A more complex bugfix can be first tested against the `master` branch while kept compatible with the `release` branch for a later integration.
Such a procedure is [described in the c:geo wiki](https://github.com/cgeo/cgeo/wiki/How-to-get-a-bug-fix-into-the-release) which can also be applied in principle to this repository.

## Releases / Versions

An overview of released beta and production versions and their changelog can be found on the [release page of this repository](https://github.com/cgeo/WhereYouGo/releases).

# Old README.md

WhereYouGo project has moved to GitHub.

* original project https://code.google.com/p/android-whereyougo/
* my project on Google Code https://code.google.com/r/biylda-whereyougo/
* this project on GitHub https://github.com/biylda/WhereYouGo/

## About

Clone of [WhereYouGo](https://code.google.com/p/android-whereyougo). Main goal is to add vector maps support and general improvements.

Uses the following projects:

* [openwig](https://github.com/biylda/openwig/tree/whereyougo)
* [mapsforge-0.3.1-with-tile-downloader-support](https://github.com/raku/mapsforge-0.3.1-with-tile-downloader-support)
* [mapsforge-map-0.3.1-with-onTap](https://github.com/jeancaffou/mapsforge-map-0.3.1-with-onTap)
* [Locus API](http://docs.locusmap.eu/doku.php?id=manual:advanced:locus_api)

### Contact
* https://groups.google.com/forum/#!forum/whereyougo
* whereyougo{at}googlegroups.com
* biylda{at}gmail.com


## Development
### Prerequisites
* [Android SDK](https://developer.android.com/sdk/index.html)
* Git

### Build
* execute
```
git clone https://github.com/biylda/WhereYouGo.git whereyougo
cd whereyougo
export ANDROID_HOME=/path/to/android-sdk
./gradlew build
```

## Translation
### New language
1. Get files with English text
	* `src/main/res/values/strings.xml`
	* `src/main/res/values/strings_pref.xml`
2. Find the language code for your language
	* Go to http://stackoverflow.com/a/30028371
	* Look up the `{code}` for your language
		* e.g. for Czech it's `cs_CZ` or just simple `cs`
3. Create a new folder `values_{code}`
	* e.g. for Czech it's`values_cs`
4. Copy the files
	* `values/strings.xml` -> `values_{code}/strings.xml`
	* `values/strings_pref.xml` -> `values_{code}/strings_pref.xml`
5. Translate
	* `values_{code}/strings.xml`
	* `values_{code}/strings_pref.xml`
6. If you are familiar with GIT, create a new merge request. Or send the folder `values_{code}` to contact mail.
	* State the original name of the language
		* e.g. for Czech it's `Čeština`
	* State under which name or nickname would you like to be credited in the app
### Existing language
1. Get files with English text
	* `src/main/res/values/strings.xml`
	* `src/main/res/values/strings_pref.xml`
2. Find the language code for your language
	* Go to http://stackoverflow.com/a/30028371
	* Look up the `{code}` for your language
		* e.g. for Czech it's `cs_CZ` or just simple `cs`
3. Get files with text in your language
	* `src/main/res/values_{code}/strings.xml`
	* `src/main/res/values_{code}/strings_pref.xml`
4. Compare the files (rows are sorted alphabetically)
	* `values/strings.xml`
	* `values_{code}/strings.xml`
5. If there are any missing rows, copy them from `values/strings.xml` to `values_{code}/strings.xml`
6. Translate
	* `values_{code}/strings.xml`
	* `values_{code}/strings_pref.xml`
7. If you are familiar with GIT, create a new merge request. Or send the folder `values_{code}` to contact mail.
	* State under which name or nickname would you like to be credited in the app
### Notifications
If you'd like to be notified about changes in English text, so that you could translate them into your language, you can use a service for change detection, e.g.
* https://www.followthatpage.com/

and register the following files for change detection:
* https://github.com/biylda/WhereYouGo/blob/master/src/main/res/values/strings.xml
* https://github.com/biylda/WhereYouGo/blob/master/src/main/res/values/strings_pref.xml

## What's new
<h4>
	<a name="#0.9.3"></a>
	0.9.3 (14. 7. 2017)
	<a href="#0.9.3" class="section_anchor"></a>
</h4>
<p>
  <ul>
        <li>Added support for animated GIF and MP4.</li>
        <li>Added Norwegian, Catalan languages</li>
        <li>Improved German, Japanese languages</li>
        <li>Clickable URLs in descriptions</li>
        <li>Task state fixed, icons improved</li>
        <li>Fixed occasional crashing when using timer or when the app is closing</li>
        <li>Improved error messages when downloading a cartridge</li>
        <li>Fixed issue with compass and timer</li>
  </ul>
</p>
<h4>
	<a name="#0.9.2"></a>
	0.9.2 (22. 3. 2017)
	<a href="#0.9.2" class="section_anchor"></a>
</h4>
<p>
  <ul>
        <li>Input can now be scanned from QR code</li>
        <li>Map can be centered to target.</li>
        <li>Fixed issue with selecting map file on Android 7.</li>
        <li>Improved French translation.</li>
  </ul>
</p>
<h4>
	<a name="#0.9.1"></a>
	0.9.1 (20. 3. 2017)
	<a href="#0.9.1" class="section_anchor"></a>
</h4>
<p>
  <ul>
        <li>Request location and write permissions on start of the application. This should fix some problems with write access.</li>
        <li>Added better message when no cartridges are available. This should help players a bit with Android SD-card permissions problematics.</li>
        <li>Save button now displays options menu to pick a slot.</li>
        <li>When clicking on a point on map, it displays a dialog with selectable text. This allows coordinate copying.</li>
        <li>Added missing Slovak strings.</li>
        <li>Logging all errors to file "error.log".</li>
        <li>Improved saving/loading time</li>
        <li>Increased timeout for slower connections when downloading cartridge</li>
        <li>Fixed: Automatic saving progress dialog and completion message.</li>
        <li>Fixed: Unable to start a cartridge from map after some cartridge was played.</li>
        <li>Fixed: Crashing on older devices and devices not supporting TLS (they still won't be able to download cartridges though).</li>
        <li>Fixed: Crashing if user doesn't have read permission when listing folders.</li>
        <li>Fixed: Crashing when user inputs invalid coordinates on map.</li>
  </ul>
</p>
<h4>
	<a name="#0.9.0"></a>
	0.9.0 (23. 4. 2016)
	<a href="#0.9.0" class="section_anchor"></a>
</h4>
<p>
  <ul>
        <li>Added saving slots. User can set the number of available saving slots in Settings->Main.
            While playing a game, user can use the menu key to show up the menu with available
            saving slots. Upon clicking on a saving slot, it saves main game and copies the savefile
            to corresponding saving slot file. When loading a game, user can select whether to start
            a New game, Main game, Backup game or from Slots. Upon starting a New game, the Main
            game savefile is not overwritten until user saves. Message about successful saving is
            shown after the game is successfully saved.
        </li>
        <li>Added settings option to use single or double click to exit the game</li>
        <li>Added settings option to start GPS automatically after the application is started.</li>
        <li>Added new settings options for units formatting (distance, speed, angle).</li>
        <li>Added start button in a dialog when user clicks on cartridge on map.</li>
        <li>Added ability to receive wherigo link via Share.</li>
        <li>Added ability to start wherigo from map after clicking on icon.</li>
        <li>Changed default Wherigo folder.</li>
        <li>Changed dialog for selecting wherigo folder, user can now select folder without *.gwc
            files present, also default folder can be set
        </li>
        <li>Changed input coordinates dialog in map to respect Lat/Lon format from settings.</li>
        <li>Fixed downloading of cartridges from wherigo.com. The server uses TLSv1.2, so it most
            likely requires Android API >= 16 (Android 4.1 Jellybean).
        </li>
        <li>Fixed: on some devices, lists with actions, zones, etc. were too narrow</li>
        <li>Fixed image stretching option</li>
        <li>Fixed angle format (negative values in azimuth).</li>
        <li>Fixed issue with info dialog in czech language.</li>
        <li>Fixed issue with string shown when no cartridge is available. This caused the app to
            crash in French.
        </li>
        <li>Fixed recovering selected map provider in options menu of a map.</li>
        <li>Fixed compatibility with the newest version of Locus.</li>
        <li>Fixed: Locus map could not be shown when no cartridges were present</li>
        <li>Improved French and Danish translation</li>
  </ul>
</p>
<h4>
	<a name="#0.8.13"></a>
	0.8.13 (23. 9. 2014)
	<a href="#0.8.13" class="section_anchor"></a>
</h4>
<p>
  <ul>
    <li>fixed: whitespaces</li>
    <li>fixed: StopSound function</li>
    <li>update: show message about invalid cartridge files</li>
    <li>update: my location on map is enabled by default</li>
    <li>update: contact</li>
  </ul>
</p>
<h4>
	<a name="#0.8.12"></a>
	0.8.12 (16. 9. 2014)
	<a href="#0.8.12" class="section_anchor"></a>
</h4>
<p>
  <ul>
    <li>fixed: unicode characters in login</li>
    <li>fixed: some bugs reported on Google Play</li>
    <li>update: translation</li>
    <li>added: start cartridge after downloading</li>
    <li>added: press BACK twice to exit the game</li>
    <li>added: backup previous savefile before saving (.bak extension)</li>
  </ul>
</p>
<h4>
	<a name="#0.8.11"></a>
	0.8.11 (3. 9. 2014)
	<a href="#0.8.11" class="section_anchor"></a>
</h4>
<p>
  <ul>
    <li>fixed: unexpected dialog closing in some versions of Android</li>
    <li>fixed: close things after clicking on a button to avoid piling up</li>
    <li>fixed: draw points and labels on top of lines</li>
    <li>fixed: allow playing sounds in headphones only</li>
    <li>update: translation (French, German)</li>
    <li>update: login credentials moved to its own category in Settings</li>
    <li>added: font size in Settings</li>
    <li>added: option to allow or disallow image stretching (Settings->Appearance)</li>
    <li>added: full support for unicode characters</li>
    <li>added: display custom icons for tasks</li>
    <li>added: support for wherigo functions Command(text), specifically StopSound, Alert</li>
  </ul>
</p>
<h4>
	<a name="#0.8.10"></a>
	0.8.10 (19. 5. 2014)
	<a href="#0.8.10" class="section_anchor"></a>
</h4>
<p>
  <ul>
    <li>fixed: some bugs</li>
    <li>fixed: close object detail after clicking on button to avoid piling up</li>
    <li>fixed: only one instance of maps</li>
    <li>update: some map providers removed, some added, added attribution on the bottom of the map</li>
    <li>added: current map provider is selected from list with radio buttons</li>
    <li>added: clickable objects on map</li>
    <li>added: links in main menu: geocaching.com, wherigo.com</li>
    <li>added: download cartridges (user needs to fill login credentials in Settings, navigate to desired wherigo via Web Browser and select open in WhereYouGo)</li>
    <li>added: long click on cartridge to show dialog for cartridge deletion</li>
    <li>added: SD card is no longer required (altough recommended), users with no SD card have to download cartridges via the newly added downloading function</li>
  </ul>
</p>
<h4>
	<a name="#0.8.9"></a>
	0.8.9 (5. 5. 2014)
	<a href="#0.8.9" class="section_anchor"></a>
</h4>
<p>
  <ul>
    <li>new Location class (not from Locus API anymore)</li>
    <li>massive refactoring inspired by YAAWP</li>
    <li>fixed: online maps were not shown until vector map file was provided</li>
    <li>fixed: map information was accessible even though vector map file was not provided</li>
    <li>update: improved german translation</li>
    <li>added: active zones in zone list are marked with tag (INSIDE)</li>
  </ul>
</p>
<h4>
	<a name="#0.8.8"></a>
	0.8.8 (3. 5. 2014)
	<a href="#0.8.8" class="section_anchor"></a>
</h4>
<p>
  <ul>
    <li>fixed: some bugs</li>
    <li>fixed: no animations in dialogs</li>
    <li>fixed: no cartridges in folder required when starting maps</li>
    <li>added: option to guide to the nearest zone point or to defined center</li>
    <li>added: arrow on map displays player's bearing</li>
    <li>added: line from my location to target</li>
    <li>added: turn on/off pins, labels on map</li>
    <li>added: center map when displaying specific object</li>
    <li>added: online maps support, remember map provider</li>
    <li>update: about application dialog</li>
  </ul>
</p>
<h4>
	<a name="#0.8.7"></a>
	0.8.7 (19. 4. 2014)
	<a href="#0.8.7" class="section_anchor"></a>
</h4>
<p>
  <ul>
    <li>fixed: choose wherigo folder</li>
    <li>update: correct debug information for openwig (device and platform)</li>
    <li>update: load game question</li>
    <li>update: error log placed in file error.log</li>
    <li>update: smaller pin icons</li>
  </ul>
</p>
<h4>
	<a name="#0.8.6"></a>
	0.8.6 (14. 4. 2014)
	<a href="#0.8.6" class="section_anchor"></a>
</h4>
<p>
  <ul>
    <li>fixed: application crashing when receiving location</li>
    <li>fixed: compass azimuth, Location on/off button</li>
    <li>update: renamed GPS to Location, since location can be received via network as well as via GPS</li>
    <li>update: code in LocationState</li>
    <li>added: location provider in Sattelite and Compass screen</li>
  </ul>
</p>
<h4>
	<a name="#0.8.5"></a>
	0.8.5 (12. 4. 2014)
	<a href="#0.8.5" class="section_anchor"></a>
</h4>
<p>
  <ul>
    <li>added: create error log when application crashes</li>
    <li>update: application description</li>
  </ul>
</p>
<h4>
	<a name="#0.8.4"></a>
	0.8.4 (10. 4. 2014)
	<a href="#0.8.4" class="section_anchor"></a>
</h4>
<p>
  <ul>
    <li>fixed: application crashing when user switches back from another application</li>
    <li>fixed: too large wherigo icons on maps</li>
    <li>added: german translation of new features</li>
  </ul>
</p>
<h4>
	<a name="#0.8.3"></a>
	0.8.3 (6. 4. 2014)
	<a href="#0.8.3" class="section_anchor"></a>
</h4>
<p>
  <ul>
    <li>fixed: application was crashing when user switched back from another application</li>
    <li>fixed: compass displays azimuth value</li>
    <li>added: save game button</li>
    <li>added: option to save game automatically</li>
    <li>added: compass and maps can handle other visible things than zones</li>
  </ul>
</p>
<h4>
	<a name="#0.8.2"></a>
	0.8.2 (29. 3. 2014)
	<a href="#0.8.2" class="section_anchor"></a>
</h4>
<p>
  <ul>
    <li>choose WhereYouGo folder</li>
    <li>vector maps can handle moving zones</li>
    <li>compass can handle moving zones</li>
    <li>compass displays target name</li>
    <li>window titles in local language</li>
    <li>icons modernization</li>
  </ul>
</p>
<h4>
	<a name="#0.8.1"></a>
	0.8.1 (23. 3. 2014)
	<a href="#0.8.1" class="section_anchor"></a>
</h4>
<p>
  <ul>
    <li>remember render schema in vector maps</li>
    <li>fixed problem in openwig with displaying items after save/load</li>
    <li>added "map" button to cartridge menu</li>
    <li>maps display all visible zones in current cartridge</li>
    <li>set GPS always on as default option</li>
  </ul>
</p>
<h4>
	<a name="#0.8.0"></a>
	0.8.0 (13. 3. 2014)
	<a href="#0.8.0" class="section_anchor"></a>
</h4>
<p>
	<ul>
		<li>added vector OSM support using <a href="https://code.google.com/p/mapsforge/">MapsForge</a></li>
		<li>added point labels in vector maps</li>
		<li>added Zone border in both Locus and vector maps</li>
		<li>added "Cancel" button when starting wherigo</li>
		<li>fixed starting a new game</li>
		<li>fixed encoding in "About application"</li>
		<li>option to display WhereYouGo icon in the statusbar while the application is running</li>
		<li>changed cartridge loading message</li>
		<li>logging messages are appended to existing log file</li>
	</ul>
</p>

<h2>
	
## TODO
* sorting objects in inventory
* add downloading from http://wherigofoundation.com
