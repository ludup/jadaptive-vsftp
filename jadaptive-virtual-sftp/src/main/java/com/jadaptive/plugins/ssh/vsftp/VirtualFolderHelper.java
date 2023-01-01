package com.jadaptive.plugins.ssh.vsftp;

import java.util.ArrayList;
import java.util.Collection;

public class VirtualFolderHelper {

	@SuppressWarnings("unchecked")
	public static <T extends VirtualFolderExtension> T getSingleBehaviour(VirtualFolder folder, Class<T> clz) {
		
		for(VirtualFolderExtension ext : folder.getExtensions()) {
			if(ext.getClass().equals(clz)) {
				if(ext.supportsMultipleInstances()) {
					throw new IllegalStateException("Unexpected call to getSingleBehaviour for a multiple instance implementation!");
				}
				return (T) ext;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends VirtualFolderExtension> Collection<T> getMultipleBehaviours(VirtualFolder folder, Class<T> clz) {
		
		ArrayList<T> results = new ArrayList<>();
		for(VirtualFolderExtension ext : folder.getExtensions()) {
			if(ext.getClass().equals(clz)) {
				if(!ext.supportsMultipleInstances()) {
					throw new IllegalStateException("Unexpected call to getMultipleBehaviours for a single instance implementation!");
				}
				results.add((T) ext);
			}
		}
		return results;
		
	}
}
