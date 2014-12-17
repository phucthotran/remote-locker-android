package com.itexplore.remotelocker.communication;

import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CommandResolve {

	public static void invoke(Object communicationObject, Command commandType, String input) {		
		Class<?> communicationClass = communicationObject.getClass();
		Method[] methods = communicationClass.getMethods();

		for(Method method : methods) {
			if(!method.isAnnotationPresent(InvokeCommand.class))
				continue;
			
			InvokeCommand methodAnotation = (InvokeCommand)method.getAnnotation(InvokeCommand.class);		
			
			if(!methodAnotation.value().equalsIgnoreCase(commandType.getValue()))
			    continue;
			
			try {
                if(methodAnotation.hasInput())
                    method.invoke(communicationObject, new Object[]{ input });
                else
                    method.invoke(communicationObject, new Object[]{});
            } catch (IllegalArgumentException e) {
                Log.w("CommandResolve.invoke()", "Error: " + e.getMessage());
                Log.e("CommandResolve.invoke()", e.getStackTrace().toString());
            } catch (IllegalAccessException e) {                    
                Log.w("CommandResolve.invoke()", "Error: " + e.getMessage());
                Log.e("CommandResolve.invoke()", e.getStackTrace().toString());
            } catch (InvocationTargetException e) {                 
                Log.w("CommandResolve.invoke()", "Error: " + e.getMessage());
                Log.e("CommandResolve.invoke()", e.getStackTrace().toString());
            }
		}
	}
	
}
