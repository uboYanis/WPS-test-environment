package utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;

public class VerifyAnnotation {

	public static boolean isWpsClass(Class c) throws Exception {
		for (Annotation annotation : c.getAnnotations()) {
			Class<? extends Annotation> type = annotation.annotationType();
			if ("org.geotools.process.factory.DescribeProcess".equals(type.getName())) {
				return true;
			}
		}
		return false;
	}

	public static boolean isWpsFunction(Method method) {
		if (method.isAnnotationPresent(DescribeProcess.class) && method.isAnnotationPresent(DescribeResult.class)) {
			return true;
		}
		return false;
	}
}
