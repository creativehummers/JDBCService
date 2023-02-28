package com.dbtransactions.transform;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public  abstract class AbstractResultTransformer<T, S> {

    public abstract T transform( S dbData);

    private static Map<String, Class<? extends AbstractResultTransformer>> resultTransformersMap = new HashMap<>();

    /*
     *   Map ResultFormat to the concrete class instances to #transform the dbData to format required
     */
    public static void register(String formatType, Class<? extends AbstractResultTransformer> transformerClass) {
        resultTransformersMap.put( formatType, transformerClass);
    }


    public static AbstractResultTransformer getResultTransformer(String formatType){
        AbstractResultTransformer transformer;
        try{
            transformer = resultTransformersMap.get( formatType).getDeclaredConstructor().newInstance();
        }  catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
        return transformer;
    }

    static {
        CSVData.init();
        JSONObjectsList.init();
        ResultSetAsListOfHashMaps.init();
        DefaultListOfStrings.init();
    }
}
