/********************************************************************************
 *                                                                              *
 *  (c) Copyright 2009 Verizon Communications USA and The Open University UK    *
 *                                                                              *
 *  This software is freely distributed in accordance with                      *
 *  the GNU Lesser General Public (LGPL) license, version 3 or later            *
 *  as published by the Free Software Foundation.                               *
 *  For details see LGPL: http://www.fsf.org/licensing/licenses/lgpl.html       *
 *               and GPL: http://www.fsf.org/licensing/licenses/gpl-3.0.html    *
 *                                                                              *
 *  This software is provided by the copyright holders and contributors "as is" *
 *  and any express or implied warranties, including, but not limited to, the   *
 *  implied warranties of merchantability and fitness for a particular purpose  *
 *  are disclaimed. In no event shall the copyright owner or contributors be    *
 *  liable for any direct, indirect, incidental, special, exemplary, or         *
 *  consequential damages (including, but not limited to, procurement of        *
 *  substitute goods or services; loss of use, data, or profits; or business    *
 *  interruption) however caused and on any theory of liability, whether in     *
 *  contract, strict liability, or tort (including negligence or otherwise)     *
 *  arising in any way out of the use of this software, even if advised of the  *
 *  possibility of such damage.                                                 *
 *                                                                              *
 ********************************************************************************/

package com.compendium.ui;

import java.io.*;
import java.util.*;
import javax.swing.*;

import org.w3c.dom.*;

import com.compendium.core.*;
import com.compendium.*;
import com.compendium.io.xml.*;


/**
 * This class manages the loading of the ReferenceNodeTypes and type matching
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class UIReferenceNodeManager {

	/** The serial version id for this class*/	 
	private static final long serialVersionUID = -2904777242533393060L;

	/**A reference to the system file path separator*/
	public final static String	sFS			= System.getProperty("file.separator");

	/**A reference to the node image directory*/
	public final static String	sPATH 		= "System"+sFS+"resources"+sFS+"ReferenceNodeIcons"+sFS;

	/** A reference to the reference node image directory.*/
	private final static String sREFERENCEPATH			= sPATH;

	/** A reference to the reference node image directory on the Mac.*/
	private final static String sMACREFERENCEPATH		= sPATH+"Mac"+sFS;
	
	/**A reference to the node image directory*/
	public final static String	sFILEPATH	= sPATH+"referencenodetypes.xml";

	/** A list of all recognised reference node type.*/
	private static Vector vtReferenceTypes		= new Vector(10);
	
	/**
	 * Return the small icon for the given reference string or else the default.
	 * @param sRefString
	 * @return ImageIcon the small icon for the given reference string.
	 */
	public static ImageIcon getSmallReferenceIcon(String sRefString) {
	
		int count = vtReferenceTypes.size();
		UIReferenceType oType = null;
		
		if (count == 1) {
			oType = (UIReferenceType)vtReferenceTypes.elementAt(0);
			if (oType.matches(sRefString)) {
				return oType.getSmallIcon();
			}
		} else if (count > 1) {
			for (int i=0; i<count; i++) {
				oType = (UIReferenceType)vtReferenceTypes.elementAt(i);
				if (oType.matches(sRefString)) {
					return oType.getSmallIcon();
				}
			}	
		}
		
		return UIImages.getNodeIcon(IUIConstants.REFERENCE_SM_ICON);
	}

	/**
	 * Return the icon for the given reference string or else the default.
	 * @param sRefString
	 * @return ImageIcon the icon for the given reference string.
	 */
	public static ImageIcon getReferenceIcon(String sRefString) {
		int count = vtReferenceTypes.size();
		UIReferenceType oType = null;
		
		for (int i=0; i<count; i++) {
			oType = (UIReferenceType)vtReferenceTypes.elementAt(i);
			if (oType.matches(sRefString)) {
				return oType.getIcon();
			}
		}
		
		return UIImages.getNodeIcon(IUIConstants.REFERENCE_ICON);
	}

	/**
	 * Return the small path icon for the given reference string or else the default.
	 * @param sRefString
	 * @return String the small icon path for the given reference string.
	 */
	public static String getSmallReferenceIconPath(String sRefString) {
	
		String sSmallIconPath = UIImages.getPath(IUIConstants.REFERENCE_ICON, true);
		return getSmallReferenceIconPath(sRefString, sSmallIconPath);
	}
	
	/**
	 * Return the small path icon for the given reference string or else the default.
	 * @param sRefString
	 * @param sDefault the default path to return is no other found.
	 * @return String the small icon path for the given reference string.
	 */
	public static String getSmallReferenceIconPath(String sRefString, String sDefault) {
	
		String sSmallIconPath = sDefault;
		
		int count = vtReferenceTypes.size();
		UIReferenceType oType = null;
		
		if (count == 1) {
			oType = (UIReferenceType)vtReferenceTypes.elementAt(0);
			if (oType.matches(sRefString)) {
				sSmallIconPath = oType.getSmallIconPath();
			}
		} else if (count > 1) {
			for (int i=0; i<count; i++) {
				oType = (UIReferenceType)vtReferenceTypes.elementAt(i);
				if (oType.matches(sRefString)) {
					sSmallIconPath = oType.getSmallIconPath();
					break;
				}
			}	
		}
		
		return sSmallIconPath;
	}

	/**
	 * Return the icon path for the given reference string or else the default.
	 * @param sRefString
	 * @return ImageIcon the icon path for the given reference string.
	 */
	public static String getReferenceIconPath(String sRefString) {
		
	    String sIconPath = UIImages.getPath(IUIConstants.REFERENCE_ICON, false);		
	    return getReferenceIconPath(sRefString, sIconPath);
	}
	
	/**
	 * Return the icon path for the given reference string or else the default.
	 * @param sRefString
	 * @return ImageIcon the icon path for the given reference string.
	 */
	public static String getReferenceIconPath(String sRefString, String sDefault) {
		
	    String sIconPath = sDefault;
		
		int count = vtReferenceTypes.size();
		UIReferenceType oType = null;
		
		if (count == 1) {
			oType = (UIReferenceType)vtReferenceTypes.elementAt(0);
			if (oType.matches(sRefString)) {
				sIconPath = oType.getIconPath();
			}
		} else if (count > 1) {
			for (int i=0; i<count; i++) {
				oType = (UIReferenceType)vtReferenceTypes.elementAt(i);
				if (oType.matches(sRefString)) {
					sIconPath = oType.getIconPath();
					break;
				}
			}	
		}
		
		return sIconPath;
	}
	
	public static boolean isReferenceNode(String sRefString) {
		
	    boolean isReferenceNode = false;
		
		int count = vtReferenceTypes.size();
		UIReferenceType oType = null;
		
		if (count == 1) {
			oType = (UIReferenceType)vtReferenceTypes.elementAt(0);
			if (oType.matches(sRefString)) {
				isReferenceNode = true;
			}
		} else if (count > 1) {
			for (int i=0; i<count; i++) {
				oType = (UIReferenceType)vtReferenceTypes.elementAt(i);
				if (oType.matches(sRefString)) {
					isReferenceNode = true;
					break;
				}
			}	
		}
		
		return isReferenceNode;
	}
	
	/**
	 * Return the name of the reference type which this ref string matches to.
	 * @param sRefString
	 * @return the name of the type that this ref string mathces to else 'Unknown'.
	 */
	public static String getReferenceTypeName(String sRefString) {
		
	    String sName = "Unknown";
		
		int count = vtReferenceTypes.size();
		UIReferenceType oType = null;
		
		if (count == 1) {
			oType = (UIReferenceType)vtReferenceTypes.elementAt(0);
			if (oType.matches(sRefString)) {
				sName = oType.getName();
			}
		} else if (count > 1) {
			for (int i=0; i<count; i++) {
				oType = (UIReferenceType)vtReferenceTypes.elementAt(i);
				if (oType.matches(sRefString)) {
					sName = oType.getName();
					break;
				}
			}	
		}
		
		return sName;
	}
	
	/**
	 * Load the reference node types data from the XML file.
	 */
	public static void loadReferenceNodeTypes() throws Exception {
		vtReferenceTypes.removeAllElements();

		try {
			File main = new File(sFILEPATH);
			XMLReader reader = new XMLReader();
			Document document = reader.read(main.getAbsolutePath(), true);

			if (document == null) {
				System.out.println("Reference Node Type data could not be loaded for "+sFILEPATH);
				throw new Exception("Reference Node Type data could not be loaded for "+sFILEPATH);
			}
			Node data = document.getDocumentElement();
			if (data == null)
				throw new Exception("Reference Node Type data could not be loaded");
			
			NodeList types = document.getElementsByTagName("reference_node_type");			
			Node node = null;		
			Node matchGroupNode = null;
			Node matchNode = null;
			UIReferenceType oReferenceType = null;
			UIReferenceMatch oMatch = null;
			UIReferenceMatchGroup oMatchGroup = null;
			int countj=0;
			int countk=0;
			int count = types.getLength();
			for (int i=0; i<count; i++) {
				node = types.item(i);
				NamedNodeMap attrs = node.getAttributes();
				
				Attr name = (Attr)attrs.getNamedItem("name");
				String sName = new String(name.getValue());

				Attr oIcon = (Attr)attrs.getNamedItem("icon");
				String sIcon = "";
				if (oIcon != null)
					sIcon = new String(oIcon.getValue());
				
				Attr oIconSmall = (Attr)attrs.getNamedItem("icon_small");
				String sIconSmall = "";
				if (oIconSmall != null)
					sIconSmall = new String(oIconSmall.getValue());

				oReferenceType = new UIReferenceType(sName, sIcon, sIconSmall);
								
				Vector matchGroups = reader.getChildrenWithTagName(node, "match_group");
				countj = matchGroups.size();
				for (int j=0; j<countj; j++) {
					matchGroupNode = (Node)matchGroups.elementAt(j);					
					NamedNodeMap innerAttrs = matchGroupNode.getAttributes();

					Attr oOperator = (Attr)innerAttrs.getNamedItem("appended_operator");
					String sOperator = "";
					if (oOperator != null) {
						sOperator = new String(oOperator.getValue());
					}
					
					oMatchGroup = new UIReferenceMatchGroup(sOperator);
					
					Vector matches = reader.getChildrenWithTagName(matchGroupNode, "match");
					countk = matches.size();
					for (int k=0; k<countk; k++) {
						matchNode = (Node)matches.elementAt(k);					
						NamedNodeMap matchAttrs = matchNode.getAttributes();

						Attr oTerm = (Attr)matchAttrs.getNamedItem("term");
						String sTerm = "";
						if (oTerm != null)
							sTerm = new String(oTerm.getValue());
	
						
						Attr oType = (Attr)matchAttrs.getNamedItem("type");
						String sType = "";
						if (oType != null)
							sType = new String(oType.getValue());
						
						oMatch = new UIReferenceMatch(sTerm, sType);
						oMatchGroup.addMatch(oMatch);						
					}	
					
					oReferenceType.addMatchGroup(oMatchGroup);
				}	
				vtReferenceTypes.addElement(oReferenceType);
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Save the reference node data to an xml file.
	 */
	public void saveData() {
		
		int count = vtReferenceTypes.size();
		UIReferenceType oType = null;
		
		for (int i=0; i<count; i++) {
			oType = (UIReferenceType)vtReferenceTypes.elementAt(i);
				
			// MOVE ASSOCIATED SMALl AND NORMAL SIZE ICONS TO THE RIGHT PLACE.
			// STANDARD ICONS

			/*String sImage = oType.getIconName();
			String newPath = UIStencilManager.sPATH+sFolderName+ProjectCompendium.sFS+sNODEIMAGEDIR+ProjectCompendium.sFS;
			if (!sFolderName.equals("") &&
					!sImage.startsWith(newPath)) {
				File imageFile = new File(sImage);
				if (imageFile.exists()) {
					try {
						newPath = newPath+imageFile.getName();
						FileInputStream fis = new FileInputStream(imageFile.getAbsolutePath());
					    FileOutputStream fos = new FileOutputStream(newPath);
				    	byte[] dataBytes = new byte[fis.available()];
				      	fis.read(dataBytes);
				      	fos.write(dataBytes);
						fis.close();
						fos.close();
						item.setImage(newPath);
					}
					catch(Exception ex) {
						System.out.println("Unable to move node image file "+imageFile.getName()+" due to:\n\n"+ex.getMessage());
					}
				}
				else {
					System.out.println("Unable to move node image file as not found: "+imageFile.getName());
				}
			}

			//SMALL ICONS
			String sPaletteImage = item.getPaletteImage();
			if (!sPaletteImage.equals("") && !sPaletteImage.equals(sImage)) {

				String newPath2 = UIStencilManager.sPATH+sFolderName+ProjectCompendium.sFS+sPALETTEIMAGEDIR+ProjectCompendium.sFS;
				if (!sFolderName.equals("") &&
						!sPaletteImage.startsWith(newPath2)) {
					File imageFile2 = new File(sPaletteImage);
					if (imageFile2.exists()) {
						try {
							newPath2 = newPath2+imageFile2.getName();
							FileInputStream fis = new FileInputStream(imageFile2.getAbsolutePath());
						    FileOutputStream fos = new FileOutputStream(newPath2);
					    	byte[] dataBytes = new byte[fis.available()];
					      	fis.read(dataBytes);
					      	fos.write(dataBytes);
							fis.close();
							fos.close();
							item.setPaletteImage(newPath2);
							item.setIcon(new ImageIcon(newPath2));
						}
						catch(Exception ex) {
							System.out.println("Unable to move palette image file "+imageFile2.getName()+" due to:\n\n"+ex.getMessage());
						}
					}
					else {
						System.out.println("Unable to move palette image file as not found: "+imageFile2.getName());
					}
				}
			}*/

			// MAC STANDARD ICONS
			/*String sBackgroundImage = item.getBackgroundImage();
			if (!sBackgroundImage.equals("")) {

				String newPath3 = sMACREFERENCEPATH;
				if (!sFolderName.equals("") &&
						!sBackgroundImage.startsWith(newPath3)) {
					File imageFile3 = new File(sBackgroundImage);
					if (imageFile3.exists()) {
						try {
							newPath3 = newPath3+imageFile3.getName();
							FileInputStream fis = new FileInputStream(imageFile3.getAbsolutePath());
						    FileOutputStream fos = new FileOutputStream(newPath3);
					    	byte[] dataBytes = new byte[fis.available()];
					      	fis.read(dataBytes);
					      	fos.write(dataBytes);
							fis.close();
							fos.close();
							item.setBackgroundImage(newPath3);
						}
						catch(Exception ex) {
							System.out.println("Unable to move background image file "+imageFile3.getName()+" due to:\n\n"+ex.getMessage());
						}
					}
					else {
						System.out.println("Unable to move background image file as not found: "+imageFile3.getName());
					}
				}
			}
			
			// MAC SMALL ICONS
			String sTemplate = item.getTemplate();
			if (!sTemplate.equals("")) {

				String newPath4 = UIStencilManager.sPATH+sFolderName+ProjectCompendium.sFS+sTEMPLATEDIR+ProjectCompendium.sFS;
				if (!sFolderName.equals("") &&
						!sTemplate.startsWith(newPath4)) {
					File file4 = new File(sTemplate);
					if (file4.exists()) {
						try {
							newPath4 = newPath4+file4.getName();
							FileInputStream fis = new FileInputStream(file4.getAbsolutePath());
						    FileOutputStream fos = new FileOutputStream(newPath4);
					    	byte[] dataBytes = new byte[fis.available()];
					      	fis.read(dataBytes);
					      	fos.write(dataBytes);
							fis.close();
							fos.close();
							item.setTemplate(newPath4);
						}
						catch(Exception ex) {
							System.out.println("Unable to move template file "+file4.getName()+" due to:\n\n"+ex.getMessage());
						}
					}
					else {
						System.out.println("Unable to move temlpate file as not found: "+file4.getName());
					}
				}
			}*/	
		}
		
		// SAVE THE XML FILE
		String data = toXML();
		try {
			FileWriter fileWriter = new FileWriter(sFILEPATH);
			fileWriter.write(data);
			fileWriter.close();
			
			// REMOAVE ANY UNREQUIRED FILES FOR ITEMS THAT HAVE BEEN DELETED
			//processDeletedItems();			
		}
		catch (IOException e) {
			ProjectCompendium.APP.displayError("Exception: (UIReferenceNodeManager.saveData) \n\n" + e.getMessage());
		}
	}
	
	/**
	 * Return an XML representation of this objects data.
	 * @return an XML representation of this objects data.
	 */
	public String toXML() {
		StringBuffer sXML = new StringBuffer(2000);
		
		sXML.append("<?xml version=\"1.0\"?>\n");
		sXML.append("<!DOCTYPE referencenodes [\n");
		sXML.append("<!ELEMENT reference_node_types (reference_node)*>\n");
		sXML.append("<!ELEMENT reference_node_type (#PCDATA | match_group)*>\n");
		sXML.append("<!ATTLIST reference_node_type\n");
		sXML.append("name CDATA #REQUIRED\n");
		sXML.append("icon CDATA #REQUIRED\n");
		sXML.append("icon_small CDATA #REQUIRED\n");
		sXML.append(">\n");
		sXML.append("<!ELEMENT match_group (#PCDATA | match)*>\n");
		sXML.append("<!ATTLIST match_group\n");
		sXML.append("appended_operator (and|or|none) \"or\"\n"); 
		sXML.append(">\n");
		sXML.append("<!ELEMENT match (#PCDATA)>\n");
		sXML.append("<!ATTLIST match\n");
		sXML.append("term CDATA #REQUIRED\n");
		sXML.append("type (starts_with|ends_with|contains|length_equals|length_less|length_more) \"ends_with\" \n");
		sXML.append(">\n");
		sXML.append("]>\n");
						
		sXML.append("<reference_node_types>\n");
	
		int count = vtReferenceTypes.size();
		UIReferenceType oType = null;
		
		for (int i=0; i<count; i++) {
			oType = (UIReferenceType)vtReferenceTypes.elementAt(i);
			sXML.append(oType.toXML());
		}	
		
		sXML.append("</reference_node_types>\n");
		return sXML.toString();
	}	
	
	/**
	 * This inner class holds information about a known reference node type.
	 * @author msb262
	 */
	private static class UIReferenceType {
						
		private String sName = "";		
		private Vector vtMatchGroups = new Vector();

		private String sIcon = "";		
		private String sSmallIcon = "";

		private ImageIcon oIcon = null;		
		private ImageIcon oSmallIcon = null;;		

		public UIReferenceType(String sName, String sIcon, String sSmallIcon) {
			this.sName = sName;
			this.sSmallIcon = sSmallIcon;
			this.sIcon = sIcon;
		}		
		
		public void addMatchGroup(UIReferenceMatchGroup oGroup) {
			vtMatchGroups.addElement(oGroup);			
		}
		
		public boolean matches(String sRefString) {
			boolean bMatchFound = false;
			
			int count = vtMatchGroups.size();
			UIReferenceMatchGroup oGroup = null;
			
			if (count == 1) {
				oGroup = (UIReferenceMatchGroup)vtMatchGroups.elementAt(0);
				bMatchFound = oGroup.matches(sRefString);				
			} else if (count > 1) {
				boolean bNextMatchFound = false;
				String sPreviousOperator = "";
				for (int i=0; i<count; i++) {
					oGroup = (UIReferenceMatchGroup)vtMatchGroups.elementAt(i);
					if (i==0) {
						sPreviousOperator = oGroup.getAppendOperator();
						bMatchFound = oGroup.matches(sRefString);						
					} else {
						bNextMatchFound = oGroup.matches(sRefString);
						if (sPreviousOperator.equals(UIReferenceMatchGroup.AND)) {
							bMatchFound = (bMatchFound && bNextMatchFound);
						} else if (sPreviousOperator.equals(UIReferenceMatchGroup.OR)) {
							bMatchFound = (bMatchFound || bNextMatchFound);
						}
						
						sPreviousOperator = oGroup.getAppendOperator();						
					}
				}	
			}
			
			return bMatchFound;
		}
	
		public String getIconPath() {
		    File file = null;
		    String sIconPath = null;		    	
			if (!ProjectCompendium.isMac) {	
				sIconPath = sREFERENCEPATH + sIcon;
				file = new File(sIconPath);				
				if (!file.exists() || file.isDirectory()) {
					sIconPath = UIImages.getPath(ICoreConstants.REFERENCE, false);
				} 
			}
			if (ProjectCompendium.isMac) {	
				String sMacIconPath = sMACREFERENCEPATH + sIcon;
				file = new File(sMacIconPath);	
				if (file.exists() && !file.isDirectory()) {
					sIconPath = sMacIconPath;
				} else {
					sIconPath = sREFERENCEPATH + sIcon;
					file = new File(sIconPath);	
					if (!file.exists() || file.isDirectory()) {
						sIconPath = UIImages.getPath(ICoreConstants.REFERENCE, false);
					}
				}
			} 
			
			return sIconPath;
		}
		
		public String getSmallIconPath() {
			File file = null;
		    String sSmallIconPath = null;   
			if (!ProjectCompendium.isMac) {	
				sSmallIconPath = sREFERENCEPATH + sSmallIcon;
				file = new File(sSmallIconPath);
				if (!file.exists() || file.isDirectory()) {
					sSmallIconPath = UIImages.getPath(ICoreConstants.REFERENCE, true);					
				} 
			} else {
				String sMacSmallIconPath = sMACREFERENCEPATH + sSmallIcon;
				file = new File(sMacSmallIconPath);				
				if (file.exists() && !file.isDirectory()) {
					sSmallIconPath = sMacSmallIconPath;
				} else {
					sSmallIconPath = sREFERENCEPATH + sSmallIcon;
					file = new File(sSmallIconPath);		
					if (!file.exists() || file.isDirectory()) {		
						sSmallIconPath = UIImages.getPath(ICoreConstants.REFERENCE, true);	
					}
				} 
			} 		
			return sSmallIconPath;
		}
		
		/**
		 * Return the standard icon for this ReferenceNode type.
		 * @return the standard icon for this ReferenceNode type.
		 */
		public ImageIcon getIcon() {
		    ImageIcon oIcon = null;
			if (this.oIcon != null) {
				oIcon = this.oIcon;
			} else {
				oIcon = new ImageIcon(getIconPath());
				this.oIcon = oIcon;
			}
			return oIcon;
		}
		
		/**
		 * Return the small icon for this ReferenceNode type.
		 * @return the small icon for this ReferenceNode type.
		 */
		 public ImageIcon getSmallIcon() {
		    ImageIcon oSmallIcon = null;
			if (this.oSmallIcon != null) {
				oSmallIcon = this.oSmallIcon;
			} else {
				oSmallIcon = new ImageIcon(getSmallIconPath());
				this.oSmallIcon = oSmallIcon;
			}
			return oSmallIcon;			
		}
		
		public String getName() {
			return sName;
		}
		
		/**
		 * Return an XML representation of this objects data.
		 * @return an XML representation of this objects data.
		 */
		public String toXML() {
			StringBuffer sXML = new StringBuffer(1000);
			sXML.append("\t<reference_node_type name=\""+sName+"\" icon=\""+sIcon+"\" icon_small=\""+sSmallIcon+"\">\n");
		
			int count = vtMatchGroups.size();
			UIReferenceMatchGroup oGroup = null;
						
			for (int i=0; i<count; i++) {
				oGroup = (UIReferenceMatchGroup)vtMatchGroups.elementAt(i);
				sXML.append(oGroup.toXML());
			}	
			
			sXML.append("\t<reference_node_type/>\n");
			return sXML.toString();
		}
	}
	
	/**
	 * This class holds data about reference node type string match group criteria.
	 * It holds a list of the match items that make up the total string match phrase.
	 * @author msb262
	 */
	private static class UIReferenceMatchGroup {
	
		public static final String AND = "and";
		public static final String OR = "or";
		public static final String NONE = "none";
		
		private String sOperator = "";
		private Vector vtMatches = new Vector();
		
		public UIReferenceMatchGroup(String sOperator) {
			this.sOperator = sOperator;
		}
		
		public void addMatch(UIReferenceMatch oMatch) {
			vtMatches.addElement(oMatch);
		}
			
		public String getAppendOperator() {
			return sOperator;			
		}
		
		public boolean matches(String sRefString) {	
			int count = vtMatches.size();
			
			UIReferenceMatch oMatch = null;
			if (count == 1) {
				oMatch = (UIReferenceMatch)vtMatches.elementAt(0);
				return oMatch.matches(sRefString);
			} else if (sOperator.equals(AND) && count > 1) {
				boolean bMatchFound = true;
				for (int i=0; i<count; i++) {					
					if (!oMatch.matches(sRefString)) {
						bMatchFound = false;
						break;
					}					
				}				
				return bMatchFound;
			} else if (sOperator.equals(OR) && count > 1) {
				boolean bMatchFound = false;
				for (int i=0; i<count; i++) {
					oMatch = (UIReferenceMatch)vtMatches.elementAt(i);
					if (oMatch.matches(sRefString)) {
						bMatchFound = true;
						break;
					}
				}				
				return bMatchFound;				
			}

			return false;
		}
		
		/**
		 * Return an XML representation of this objects data.
		 * @return an XML representation of this objects data.
		 */
		public String toXML() {
			UIReferenceMatch oMatch = null;
			int count = vtMatches.size();			
			StringBuffer sXML = new StringBuffer(500);
			sXML.append("\t\t<match_group appended_operator=\""+sOperator+"\">\n");
			for (int i=0; i<count; i++) {
				oMatch = (UIReferenceMatch)vtMatches.elementAt(i);
				sXML.append(oMatch.toXML());
			}		
			sXML.append("\t\t</match_group>\n");			
			return sXML.toString();
		}
	}
	
	/**
	 * This class holds data about reference node type string match criteria 
	 * @author msb262
	 */
	private static class UIReferenceMatch {
		
		private static final String ENDS_WITH = "ends_with";
		private static final String STARTS_WITH = "starts_with";
		private static final String CONTAINS = "contains";
		private static final String LENGTH_EQUALS = "length_equals";
		private static final String LENGTH_LESS_THAN = "length_less";
		private static final String LENGTH_MORE_THAN = "length_more";
		private static final String IS_DIRECTORY = "isDirectory";

		private String sTerm = "";
		private int nLength = 0;
		private String sType = "";
		
		public UIReferenceMatch(String sTerm, String sType) {
			this.sTerm = sTerm;
			this.sType = sType;	
			
			if (sType.equals(LENGTH_EQUALS) || 
					sType.equals(LENGTH_LESS_THAN) || 
					sType.equals(LENGTH_MORE_THAN)) {
				
				try {
					nLength = (new Integer(sTerm)).intValue();
				} catch (NumberFormatException num) {}
			}
		}
		
		/**
		 * Return if the passed string matches this match condition.
		 * @param sRefString
		 * @return
		 */
		public boolean matches(String sRefString) {
			
			String sLowerRefString = sRefString.toLowerCase();	
			int length = sLowerRefString.length();
			
			if (sType.equals(ENDS_WITH) && sLowerRefString.endsWith(sTerm)) { 
				return true;				
			} else if (sType.equals(STARTS_WITH) && sLowerRefString.startsWith(sTerm)) {
				return true;				
			} else if (sType.equals(CONTAINS) && sLowerRefString.indexOf(sTerm) > -1) {
				return true;			
			} else if (sType.equals(LENGTH_EQUALS) && length == nLength) {
				return true;
			} else if (sType.equals(LENGTH_MORE_THAN) && length > nLength) {
				return true;
			} else if (sType.equals(LENGTH_LESS_THAN) && length < nLength) {
				return true;
			} else if (sType.equals(IS_DIRECTORY) && (new File(sRefString).isDirectory())) {
				return true;
			}
			
			return false;
		}
		
		/**
		 * Return an XML representation of this objects data.
		 * @return an XML representation of this objects data.
		 */		
		public String toXML() {			
			String sXML = "\t\t\t<match term=\""+sTerm+"\" type=\""+sType+"\" />\n";			
			return sXML;
		}
	}
}
