package com.jadaptive.plugins.vsftp.s3;

import java.util.Map;

import software.amazon.awssdk.regions.Region;

public enum S3Region {

	AP_SOUTH_2(Region.AP_SOUTH_2), 
	AP_SOUTH_1(Region.AP_SOUTH_1), 
	EU_SOUTH_1(Region.EU_SOUTH_1), 
	EU_SOUTH_2(Region.EU_SOUTH_2), 
	US_GOV_EAST_1(Region.US_GOV_EAST_1), 
	ME_CENTRAL_1(Region.ME_CENTRAL_1), 
	CA_CENTRAL_1(Region.CA_CENTRAL_1), 
	EU_CENTRAL_1(Region.EU_CENTRAL_1),
	US_ISO_WEST_1(Region.US_ISO_WEST_1), 
	EU_CENTRAL_2(Region.EU_CENTRAL_2), 
	US_WEST_1(Region.US_WEST_1), 
	US_WEST_2(Region.US_WEST_2), 
	AF_SOUTH_1(Region.AF_SOUTH_1), 
	EU_NORTH_1(Region.EU_NORTH_1), 
	EU_WEST_3(Region.EU_WEST_3), 
	EU_WEST_2(Region.EU_WEST_2), 
	EU_WEST_1(Region.EU_WEST_1),
	AP_NORTHEAST_3(Region.AP_NORTHEAST_3), 
	AP_NORTHEAST_2(Region.AP_NORTHEAST_2), 
	AP_NORTHEAST_1(Region.AP_NORTHEAST_1), 
	ME_SOUTH_1(Region.ME_SOUTH_1), 
	SA_EAST_1(Region.SA_EAST_1), 
	AP_EAST_1(Region.AP_EAST_1), 
	CN_NORTH_1(Region.CN_NORTH_1), 
	US_GOV_WEST_1(Region.US_GOV_WEST_1),
	AP_SOUTHEAST_1(Region.AP_SOUTHEAST_1), 
	AP_SOUTHEAST_2(Region.AP_SOUTHEAST_2), 
	US_ISO_EAST_1(Region.US_ISO_EAST_1), 
	AP_SOUTHEAST_3(Region.AP_SOUTHEAST_3), 
	AP_SOUTHEAST_4(Region.AP_SOUTHEAST_4), 
	US_EAST_1(Region.US_EAST_1), 
	US_EAST_2(Region.US_EAST_2), 
	CN_NORTHWEST_1(Region.CN_NORTHWEST_1),
	US_ISOB_EAST_1(Region.US_ISOB_EAST_1), 
	AWS_GLOBAL(Region.AWS_GLOBAL), 
	AWS_CN_GLOBAL(Region.AWS_CN_GLOBAL), 
	AWS_US_GOV_GLOBAL(Region.AWS_US_GOV_GLOBAL), 
	AWS_ISO_GLOBAL(Region.AWS_ISO_GLOBAL), 
	AWS_ISO_B_GLOBAL(Region.AWS_ISO_B_GLOBAL);
	
	private Region sdkRegion;
	
	private S3Region(Region sdkRegion) {
		this.sdkRegion = sdkRegion;
	}
	public Region getSDKRegion() {
		return sdkRegion;
	}
}
