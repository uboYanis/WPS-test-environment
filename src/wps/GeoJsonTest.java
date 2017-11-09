package wps;

import org.geoserver.wps.gs.GeoServerProcess;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.geotools.process.factory.StaticMethodsProcessFactory;
import org.geotools.text.Text;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

@DescribeProcess(title = "WPS yanis", description = "WPS yanis Hello World")
public class GeoJsonTest extends StaticMethodsProcessFactory<GeoJsonTest> implements GeoServerProcess {

	public GeoJsonTest() {
		super(Text.text("WPS Geometry geojson test "), "wpsTest", GeoJsonTest.class);
	}

	@DescribeProcess(title = "WPS geometry", description = "geometry")
	@DescribeResult(name = "geometry", description = "the resulting geometry")
	public static Geometry getGeometry(
			@DescribeParameter(name = "geometry input", description = "the input gemetry") final Geometry geometry) {
		Geometry geom = null;
		GeometryFactory factory = new GeometryFactory();
		if (geometry instanceof LineString) {
			Coordinate[] coordinates = new Coordinate[geometry.getCoordinates().length];
			coordinates = geometry.getCoordinates();
			for (Coordinate coordinate : coordinates) {
				coordinate.x = coordinate.x + 50;
				coordinate.y = coordinate.y + 20;
			}
			geom = factory.createLineString(coordinates);
		}

		if (geometry instanceof Polygon) {
			Coordinate[] coordinates = new Coordinate[geometry.getCoordinates().length];
			coordinates = geometry.getCoordinates();
			for (Coordinate coordinate : coordinates) {
				coordinate.x = coordinate.x + 50;
				coordinate.y = coordinate.y + 20;
			}
			geom = factory.createPolygon(coordinates);
		}

		if (geometry instanceof Point) {
			Coordinate coordinate = geometry.getCoordinate();
			coordinate.x = coordinate.x + 50;
			coordinate.y = coordinate.y + 20;
			geom = factory.createPoint(coordinate);
		}

		if (geometry instanceof MultiPoint) {
			Coordinate[] listPoint = new Coordinate[geometry.getNumGeometries()];
			for (int i = 0; i < geometry.getNumGeometries(); i++) {
				Point point = (Point) geometry.getGeometryN(i);
				Coordinate coordinate = point.getCoordinate();
				coordinate.x = coordinate.x + 50;
				coordinate.y = coordinate.y + 20;
				listPoint[i] = coordinate;
			}
			geom = factory.createMultiPoint(listPoint);
		}

		if (geometry instanceof MultiLineString) {
			LineString[] listLineString = new LineString[geometry.getNumGeometries()];
			for (int i = 0; i < geometry.getNumGeometries(); i++) {
				LineString linestring = (LineString) geometry.getGeometryN(i);
				Coordinate[] coordinates = new Coordinate[linestring.getCoordinates().length];
				coordinates = linestring.getCoordinates();
				for (Coordinate coordinate : coordinates) {
					coordinate.x = coordinate.x + 50;
					coordinate.y = coordinate.y + 20;
				}
				listLineString[i] = factory.createLineString(coordinates);
			}

			geom = factory.createMultiLineString(listLineString);
		}

		if (geometry instanceof MultiPolygon) {
			Polygon[] polygonList = new Polygon[geometry.getNumGeometries()];
			for (int i = 0; i < geometry.getNumGeometries(); i++) {
				Polygon polygon = (Polygon) geometry.getGeometryN(i);
				Coordinate[] coordinates = new Coordinate[polygon.getCoordinates().length];
				coordinates = polygon.getCoordinates();
				for (Coordinate coordinate : coordinates) {
					coordinate.x = coordinate.x + 50;
					coordinate.y = coordinate.y + 20;
				}
				polygonList[i] = factory.createPolygon(coordinates);
			}

			geom = factory.createMultiPolygon(polygonList);
		}

		return geom;

	}

	@DescribeProcess(title = "WPS FeaturesCollection", description = "FeaturesCollection")
	@DescribeResult(name = "featureCollection", description = "the resulting FeatureCollection")
	public static FeatureCollection<SimpleFeatureType, SimpleFeature> getFeatures(
			@DescribeParameter(name = "featureCollection input", description = "the input featureCollection") final FeatureCollection<SimpleFeatureType, SimpleFeature> inputFeatureCollection) {

		FeatureCollection<SimpleFeatureType, SimpleFeature> outputFeartureCollection = inputFeatureCollection;
		FeatureIterator<SimpleFeature> iterator = outputFeartureCollection.features();
		while (iterator.hasNext()) {
			SimpleFeature sf = iterator.next();
			Geometry geometry = (Geometry) sf.getDefaultGeometry();
			if (geometry instanceof LineString) {
				for (Coordinate coordinate : geometry.getCoordinates()) {
					coordinate.x = coordinate.x + 50;
					coordinate.y = coordinate.y + 50;
				}
			}
			if (geometry instanceof Polygon) {
				for (Coordinate coordinate : geometry.getCoordinates()) {
					coordinate.x = coordinate.x + 20;
					coordinate.y = coordinate.y + 50;
				}
			}

			if (geometry instanceof Point) {
				Coordinate coordinate = geometry.getCoordinate();
				coordinate.x = coordinate.x + 50;
				coordinate.y = coordinate.y + 50;
			}
		}

		return outputFeartureCollection;
	}

}