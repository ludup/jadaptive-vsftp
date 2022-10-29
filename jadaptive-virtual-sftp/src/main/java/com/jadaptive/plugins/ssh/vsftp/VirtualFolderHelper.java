package com.jadaptive.plugins.ssh.vsftp;

import java.util.ArrayList;
import java.util.Collection;

public class VirtualFolderHelper {

	@SuppressWarnings("unchecked")
	public static <T extends VirtualFolderBehaviour> T getSingleBehaviour(VirtualFolder folder, Class<T> clz) {
		
		for(VirtualFolderBehaviour behaviour : folder.getBehaviours()) {
			if(behaviour.getClass().equals(clz)) {
				if(behaviour.supportsMultipleInstances()) {
					throw new IllegalStateException("Unexpected call to getSingleBehaviour for a multiple instance implementation!");
				}
				return (T) behaviour;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends VirtualFolderBehaviour> Collection<T> getMultipleBehaviours(VirtualFolder folder, Class<T> clz) {
		
		ArrayList<T> results = new ArrayList<>();
		for(VirtualFolderBehaviour behaviour : folder.getBehaviours()) {
			if(behaviour.getClass().equals(clz)) {
				if(!behaviour.supportsMultipleInstances()) {
					throw new IllegalStateException("Unexpected call to getMultipleBehaviours for a single instance implementation!");
				}
				results.add((T) behaviour);
			}
		}
		return results;
		
	}
}
