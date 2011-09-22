/*
 * Copyright (c) 2010-2011, Monash e-Research Centre
 * (Monash University, Australia)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of the Monash University nor the names of its
 * 	  contributors may be used to endorse or promote products derived from
 * 	  this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package au.edu.monash.merc.capture.adapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import au.edu.monash.merc.capture.domain.Collection;
import au.edu.monash.merc.capture.domain.Dataset;
import au.edu.monash.merc.capture.domain.GlobalMetadata;
import au.edu.monash.merc.capture.domain.MetaAttribute;
import au.edu.monash.merc.capture.domain.MetaVariable;
import au.edu.monash.merc.capture.exception.DataCaptureException;
import au.edu.monash.merc.capture.util.CaptureUtil;

/**
 * NetCDFDataCaptureAdapter class which implements the DataCaptureAdapter interface provides the metadata extraction
 * function
 * 
 * @author Simon Yu - Xiaoming.Yu@monash.edu
 * @version v2.0
 * @since v1.0
 * 
 */
public class NetCDFDataCaptureAdapter implements DataCaptureAdapter {

	private static String SITE_NAME = "SiteName";

	private static String RUN_DATETIME_TAG = "RunDateTime";

	private static String XLMOD_DATETIME_TAG = "xlModDateTime";

	private static String TITLE = "title";

	private static String SPEC = "specification";

	private static String NET_CDF_FILE_LEVEL = "Level";

	private static int BUFFER_SIZE = 10240;

	@Override
	public Dataset caputreData(String name, String fileFullPathName, boolean extractRequired, boolean globalAttrbuteOnly) throws DataCaptureException {
		Dataset ds = new Dataset();
		ds.setName(name);
		ds.setExtracted(extractRequired);
		// if extracting the metadata is not required, just return the dataset object.
		if (!extractRequired) {
			return ds;
		}
		// start to extract the metadata
		NetcdfFile ncfile = null;
		// the NetcdfFileWriteable only works for net-cdf file
		// NetcdfFileWriteable ncfile = null;
		try {
			// ncfile = NetcdfFileWriteable.openExisting(fileFullPathName, false);
			ncfile = NetcdfFile.open(fileFullPathName, BUFFER_SIZE, null);
			// fetch global attributes
			List<Attribute> globalAttrs = ncfile.getGlobalAttributes();
			copyGlobalMetaData(globalAttrs, ds);
			// if not the global attribute only, then extract other variables and attributes
			if (!globalAttrbuteOnly) {
				// fetch variables
				List<Variable> vars = ncfile.getVariables();
				copyVariables(vars, ds);
			}
		} catch (IOException e) {
			throw new DataCaptureException(e);
		} finally {
			if (ncfile != null) {
				try {
					ncfile.close();
					// remove any grib2 index file if any
					File gbx8file = new File(fileFullPathName + ".gbx8");
					if (gbx8file.exists()) {
						gbx8file.delete();
					}
				} catch (IOException e) {
					// ignore whatever caught
				}
			}
		}

		return ds;
	}

	protected void copyGlobalMetaData(List<Attribute> globalAtts, Dataset ds) {
		List<GlobalMetadata> persist_globalAttrs = new ArrayList<GlobalMetadata>();
		for (Attribute att : globalAtts) {
			GlobalMetadata persist_gatt = new GlobalMetadata();
			String name = att.getName();
			String value = att.getStringValue();

			persist_gatt.setName(name);
			persist_gatt.setValue(value);
			if (name.equalsIgnoreCase(SITE_NAME)) {
				setSiteInfo(value, ds);
			}
			if (name.equalsIgnoreCase(RUN_DATETIME_TAG)) {
				setRunDateTimeStamp(value, ds);
			}

			if (name.equalsIgnoreCase(XLMOD_DATETIME_TAG)) {
				setXlModDateTimeStamp(value, ds);
			}

			if (name.equalsIgnoreCase(TITLE)) {
				setTitle(value, ds);
			}
			if (name.equalsIgnoreCase(SPEC)) {
				setSpec(value, ds);
			}
			if (name.equalsIgnoreCase(NET_CDF_FILE_LEVEL)) {
				setNetCDFLevel(value, ds);
			}

			persist_gatt.setDataset(ds);
			persist_globalAttrs.add(persist_gatt);
		}
		ds.setGlobalMetadata(persist_globalAttrs);
	}

	protected void copyVariables(List<Variable> vars, Dataset ds) {

		List<MetaVariable> metaVarList = new ArrayList<MetaVariable>();

		boolean strict = false;
		boolean useFullName = true;
		// Formatter buf = new Formatter();

		for (Variable v : vars) {
			MetaVariable metavar = new MetaVariable();

			useFullName = useFullName && !strict;
			String name = useFullName ? v.getName() : v.getShortName();
			if (strict) {
				name = NetcdfFile.escapeName(name);
			}
			metavar.setName(name);
			metavar.setDataType(v.getDataType().getClassType().getSimpleName());

			metavar.setNameDimensions(formatNameDimensionsa(v.getNameAndDimensions()));
			// copy the attributes
			copyAttributes(v, metavar);
			metavar.setDataset(ds);
			metaVarList.add(metavar);
		}
		ds.setMetaVariables(metaVarList);
	}

	private String formatNameDimensionsa(String nameDimensions) {
		String tmp = StringUtils.replace(nameDimensions, "(", " [");
		return StringUtils.replace(tmp, ")", "]");
	}

	// not use it, replaced by name and dimensions string value.
	// protected void copyDimensions(Variable v, MetaVariable metavar, boolean strict) {
	// List<Dimension> dimensions = v.getDimensions();
	// List<MetaDimension> metaDimList = new ArrayList<MetaDimension>();
	//
	// for (Dimension myd : dimensions) {
	// MetaDimension mdim = new MetaDimension();
	//
	// String dimName = myd.getName();
	// if ((dimName != null) && strict) {
	// dimName = NetcdfFile.escapeName(dimName);
	// }
	// mdim.setName(dimName);
	//
	// if (myd.isShared()) {
	// mdim.setValue(String.valueOf(myd.getLength()));
	// }
	// mdim.setMetaVariable(metavar);
	// metaDimList.add(mdim);
	// }
	// metavar.setMetaDimensions(metaDimList);
	// }

	protected void copyAttributes(Variable v, MetaVariable metavar) {
		List<MetaAttribute> metaAttsList = new ArrayList<MetaAttribute>();
		List<Attribute> attribs = v.getAttributes();
		for (Attribute at : attribs) {
			MetaAttribute metaAtt = new MetaAttribute();
			metaAtt.setName(at.getName());

			// set the data type
			metaAtt.setDataType(at.getDataType().getClassType().getSimpleName());

			String atStr = at.toString();
			if (atStr != null) {
				metaAtt.setValue(StringUtils.substringAfter(atStr, "="));
			} else {
				metaAtt.setValue("na");
			}
			// metaAtt.setValue(at.getStringValue());
			metaAtt.setMetaVariable(metavar);
			metaAttsList.add(metaAtt);
		}
		metavar.setMetaAttributes(metaAttsList);
	}

	/**
	 * Set Site Name
	 * 
	 * @param siteName
	 * @param ds
	 */
	private void setSiteInfo(String siteName, Dataset ds) {
		ds.setSiteName(siteName);
	}

	/**
	 * Set the timestamp
	 * 
	 * @param timestamp
	 * @param ds
	 */
	private void setRunDateTimeStamp(String timestamp, Dataset ds) {
		String tmTag = StringUtils.substring(timestamp, 0, 10);
		ds.setRunDateTimeTag(tmTag);
		Date createdTime = CaptureUtil.formatDate(timestamp);
		ds.setRunDateTime(createdTime);
	}

	/**
	 * Set the timestamp
	 * 
	 * @param timestamp
	 * @param ds
	 */
	private void setXlModDateTimeStamp(String timestamp, Dataset ds) {
		String tmTag = StringUtils.substring(timestamp, 0, 10);
		ds.setXlModDateTimeTag(tmTag);
		Date createdTime = CaptureUtil.formatDate(timestamp);
		ds.setXlModDateTime(createdTime);
	}

	/**
	 * Set Title of dataset
	 * 
	 * @param title
	 * @param ds
	 */
	private void setTitle(String title, Dataset ds) {
		ds.setTitle(title);
	}

	/**
	 * Set specification of dataset
	 * 
	 * @param spec
	 * @param ds
	 */
	private void setSpec(String spec, Dataset ds) {
		ds.setSpecification(spec);
	}

	private void setNetCDFLevel(String level, Dataset ds) {
		ds.setNetCDFLevel(level);
	}

	public static void main(String[] args) throws Exception {

		String filename = "./testData/AdelaideRiver_2008_L3.nc";
		// String filename = "./testData/ei_oper_an_pl_15x15_802";
		// String filename = "./testData/ei_mnth_fc_sfc_15x15_90N0E90S3585E_19890101_20051201";

		// ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
		// InputStream in = new BufferedInputStream(new FileInputStream(filename));
		// IO.copy(in, bos);

		NetCDFDataCaptureAdapter adapter = new NetCDFDataCaptureAdapter();
		System.out.println("============ start to read file: " + filename);
		Dataset ds = adapter.caputreData("ei_oper_an_pl_15x15_802", filename, true, false);
		// Dataset ds = adapter.caputreData("ei_mnth_fc_sfc_15x15_90N0E90S3585E_19890101_20051201", bos.toByteArray());
		Collection col = new Collection();
		col.setName(ds.getSiteName());
		// col.setTimestampTag(ds.getTimestampTag());

		System.out.println("---------------- Collection ---------------------------");
		System.out.println("collection name: " + col.getName());
		// System.out.println("collection timestamp: " + col.getTimestampTag());

		System.out.println("\n------------  Dataset -------------------------------");
		System.out.println("Dataset name: " + ds.getName());

		System.out.println("Dataset Level: " + ds.getNetCDFLevel());

		System.out.println("\n------------  Global Attributes -------------------------------");

		List<GlobalMetadata> persist_globalAttrs = ds.getGlobalMetadata();
		for (GlobalMetadata gatt : persist_globalAttrs) {
			System.out.println("global attribute: " + gatt.getName() + "=" + gatt.getValue());
		}
		List<MetaVariable> persist_metaVariables = ds.getMetaVariables();

		System.out.println("\n--------------------------------------------\n");
		for (MetaVariable mv : persist_metaVariables) {
			System.out.println("\n --------------- Variable -------------------------------------");
			System.out.println("Variable name : " + mv.getName());
			System.out.println("Variable name dimensions : " + mv.getNameDimensions());
			System.out.println("Variable data type : " + mv.getDataType());

			List<MetaAttribute> metaAttrList = mv.getMetaAttributes();
			System.out.println("\n     ------------------ Attributes ------------------------");
			for (MetaAttribute matt : metaAttrList) {
				System.out.println("      Attribute data type: " + matt.getDataType() + ", Attribute name : " + matt.getName() + "="
						+ matt.getValue());
			}
		}

	}

}
