WhereYouGo
==========

WhereYouGo is an unofficial client for Wherigo Geocaching. It displays online and offline vector maps using Mapsforge library, alternatively Locus can be used to display maps.

## About

This is a clone of this [WhereYouGo repository](https://github.com/biylda/WhereYouGo/) as it was found unmaintained and was (after confirmation of original author) transferred to here.
Development can be continued seamless as we got the ability and allowance to publish the app under the original accounts and signing keys.

## Contact
As there are no specific support and contact channels for this project yet, please use the issue tracker of this repository to get in contact with the development community for the time being.

If PM contact is needed please contact us at whereyougo@cgeo.org 

## Development

### Build
to be completed

### Branches
- **master** is for development of new features.
- **release** is for all bug fixes of already existing features. So if a bug is reported in released version, it should be fixed on this branch (and merged to master afterwards).

A more complex bugfix can be first tested against the `master` branch while kept compatible with the `release` branch for a later integration.
Such a procedure is [described in the c:geo wiki](https://github.com/cgeo/cgeo/wiki/How-to-get-a-bug-fix-into-the-release) which can also be applied in principle to this repository.

### Dependencies
Uses the following projects:

* [openwig](https://github.com/cgeo/openwig)
* [mapsforge-0.3.1-with-tile-downloader-support](https://github.com/raku/mapsforge-0.3.1-with-tile-downloader-support)
* [mapsforge-map-0.3.1-with-onTap](https://github.com/jeancaffou/mapsforge-map-0.3.1-with-onTap)
* [Locus API](http://docs.locusmap.eu/doku.php?id=manual:advanced:locus_api)

## Translation

We started using [Crowdin](https://crowdin.com/project/whereyougo) to support translations.
At this moment export/import to/from Crowdin is done manually by a member of the development team.

Localization status: [![Crowdin](https://badges.crowdin.net/whereyougo/localized.svg)](https://crowdin.com/project/whereyougo)

## Releases / Versions

An overview of released beta and production versions and their changelog can be found on the [release page of this repository](https://github.com/cgeo/WhereYouGo/releases).

Releases for [Google Play](https://play.google.com/store/apps/details?id=menion.android.whereyougo) will be done from branch **release** and can be build by team members using the [CI environment](https://ci.cgeo.org). 

The version code / version name is automatically derived and inccluded during the build process using the date, the release was built on the CI.
Once built the APK can either be directly uploaded to Google Play as production version or as a beta version and later on be promoted to a production version (under the same version code / version name).
