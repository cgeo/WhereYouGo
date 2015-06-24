WhereYouGo
==========

WhereYouGo project has moved to GitHub.

original project https://code.google.com/p/android-whereyougo/
<br>
my project on Google Code https://code.google.com/r/biylda-whereyougo/
<br>
this project on GitHub https://github.com/biylda/WhereYouGo/

<h2>
	<a name="About"></a>
	About
	<a href="#About" class="section_anchor"></a>
</h2>
<p>
	Clone of <a href="https://code.google.com/p/android-whereyougo/" rel="nofollow">WhereYouGo</a>. Main goal is to add vector maps support and general improvements.
</p>
<p>
  Uses the following projects:
  <ul>
    <li><a href="https://code.google.com/r/biylda-openwig/" rel="nofollow">biylda-openwig</a></li>
    <li><a href="https://github.com/raku/mapsforge-0.3.1-with-tile-downloader-support" rel="nofollow">mapsforge-0.3.1-with-tile-downloader-support</a></li>
    <li><a href="https://github.com/jeancaffou/mapsforge-map-0.3.1-with-onTap" rel="nofollow">mapsforge-map-0.3.1-with-onTap</a></li>
  </ul>
</p>
<p>
  Contact: biylda{at}gmail.com
</p>
<h2>
	<a name="#Whats_new"></a>
	What's new
	<a href="#Whats_new" class="section_anchor"></a>
</h2>
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
	<a name="#TODO"></a>
	TODO
	<a href="#TODO" class="section_anchor"></a>
</h2>
<p>
 <ol>
  <li><ul>
  </ul></li>
  <li><ul>
    <li>sorting objects in inventary</li>
    <li>saving to and loading from custom files</li>
  </ul></li>
 </ol>
</p>
