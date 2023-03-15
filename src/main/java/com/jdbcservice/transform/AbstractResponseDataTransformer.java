package com.jdbcservice.transform;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public  abstract class AbstractResponseDataTransformer<T, S> {

    public abstract T transform( S dbData);

    private static Map<String, Class<? extends AbstractResponseDataTransformer>> resultTransformersMap = new HashMap<>();

    /*
     *   Map ResultFormat to the concrete class instances to #transform the dbData to format required
     */
    public static void register(String formatType, Class<? extends AbstractResponseDataTransformer> transformerClass) {
        resultTransformersMap.put( formatType, transformerClass);
    }


    public static AbstractResponseDataTransformer getResultTransformer(String formatType){
        AbstractResponseDataTransformer transformer;
        try{
            transformer = resultTransformersMap.get( formatType).getDeclaredConstructor().newInstance();
        }  catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
        return transformer;
    }

    static {
        TransformToCSVData.init();
        TransformToJSONObjectsList.init();
        TransformResultSetAsListOfHashMaps.init();
        TransformToDefaultListOfStrings.init();
    }
}
