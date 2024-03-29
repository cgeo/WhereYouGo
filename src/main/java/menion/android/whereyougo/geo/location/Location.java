package menion.android.whereyougo.geo.location;

public class Location extends locus.api.objects.extra.Location {

    public Location() {
        super();
    }

    public Location(android.location.Location loc) {
        this(loc.getLatitude(), loc.getLongitude());
        setTime(loc.getTime());
        if (loc.hasAccuracy()) {
            setAccuracyHor(loc.getAccuracy());
        }
        if (loc.hasAltitude()) {
            setAltitude(loc.getAltitude());
        }
        if (loc.hasBearing()) {
            setBearing(loc.getBearing());
        }
        if (loc.hasSpeed()) {
            setSpeed(loc.getSpeed());
        }
    }

    public Location(Location loc) {
        super(loc);
    }

    public Location(double lat, double lon) {
        super(lat, lon);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (!(obj instanceof Location)) return false;
        Location other = (Location) obj;
        return getLatitude() == other.getLatitude() && getLongitude() == other.getLongitude();
    }
}
