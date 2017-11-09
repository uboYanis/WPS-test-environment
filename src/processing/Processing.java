package processing;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Geometry;

public class Processing {
	ClassLoader classLoader = Processing.class.getClassLoader();

	public Class getClassInst(String packageName, String nameClass) {
		Class aClass = null;
		try {
			return aClass = classLoader.loadClass(packageName + "." + nameClass);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return aClass;
	}

	public FeatureCollection<SimpleFeatureType, SimpleFeature> useMethodeParamFeature(Object c, String methode,
			FeatureCollection<SimpleFeatureType, SimpleFeature> param) throws Exception {
		Class curentClass = c.getClass();
		Method ss1Method = curentClass.getMethod(methode, new Class[] { FeatureCollection.class });
		return (FeatureCollection<SimpleFeatureType, SimpleFeature>) ss1Method.invoke(c, new Object[] { param });
	}

	public Geometry useMethodeParamGeo(Object c, String methode, Geometry param) throws Exception {
		Class curentClass = c.getClass();
		Method ss1Method = curentClass.getMethod(methode, new Class[] { Geometry.class });
		return (Geometry) ss1Method.invoke(c, new Object[] { param });
	}

	public Method[] getListMethode(Class c) {

		Method[] methods1 = c.getDeclaredMethods();
		Method[] methods2 = c.getMethods();

		Set<Method> s1 = new HashSet<Method>(Arrays.asList(methods1));
		Set<Method> s2 = new HashSet<Method>(Arrays.asList(methods2));
		s1.retainAll(s2);

		return s1.toArray(new Method[s1.size()]);
	}
}
