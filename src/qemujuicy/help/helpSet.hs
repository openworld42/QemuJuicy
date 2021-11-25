<?xml version='1.0' encoding='ISO-8859-1' ?>
<!DOCTYPE helpset
  PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 2.0//EN"
         "http://java.sun.com/products/javahelp/helpset_2_0.dtd">
<helpset version="2.0">
   <!-- title --> 
   <title>QemuJuicy - Help</title>
			
   <!-- maps --> 
   <maps>
     <homeID>top</homeID>
     <mapref location="map.jhm" />
   </maps>
	
   <!-- views --> 
   <view xml:lang="en" mergetype="javax.help.UniteAppendMerge">
      <name>TOC</name>
      <label>Table Of Contents</label>
      <type>javax.help.TOCView</type>
      <data>IdeHelpTOC.xml</data>
   </view>
   
<!--
   <view xml:lang="en" mergetype="javax.help.SortMerge">
      <name>Index</name>
      <label>Index</label>
      <type>javax.help.IndexView</type>
      <data>IdeHelpIndex.xml</data>
   </view>
	
   <view xml:lang="en">
      <name>Search</name>
      <label>Search</label>
      <type>javax.help.SearchView</type>
         <data engine="com.sun.java.help.search.DefaultSearchEngine">
         JavaHelpSearch
         </data>
   </view>
 --> 
 
   <!-- A glossary navigator
   <view  mergetype="javax.help.SortMerge">
      <name>glossary</name>
      <label>Glossary</label>
      <type>javax.help.GlossaryView</type>
      <data>glossary.xml</data>
   </view>
 -->
 
   <!-- A favorites navigator
   <view>
      <name>favorites</name>
      <label>Favorites</label>
      <type>javax.help.FavoritesView</type>
   </view>
 -->
 
   <!-- presentation windows -->

   <!-- This window is the default one for the helpset. 
     *  It is a tri-paned window because displayviews, not
     *  defined, defaults to true and because a toolbar is defined.
   -->
   <presentation default=true>
       <name>main window</name>
       <size width="1100" height="630" /> 
       <location x="150" y="150" />
       <title>QemuJuicy Help</title>
       <toolbar>
           <helpaction>javax.help.BackAction</helpaction>
           <helpaction>javax.help.ForwardAction</helpaction>
           <helpaction image="homeicon">javax.help.HomeAction</helpaction>
       </toolbar>
   </presentation>

   <!-- This window is simpler than the main window. 
     *  It's intended to be used a secondary window.
     *  It has no navigation pane or toolbar.
   -->
   <presentation displayviews=false>
       <name>secondary window</name>
       <size width="200" height="200" /> 
       <location x="200" y="200" />
   </presentation>

   <!-- implementation section -->
   <impl>
      <helpsetregistry helpbrokerclass="javax.help.DefaultHelpBroker" />
      <viewerregistry viewertype="text/html" 
         viewerclass="com.sun.java.help.impl.CustomKit" />
      <viewerregistry viewertype="text/xml" 
         viewerclass="com.sun.java.help.impl.CustomXMLKit" />
   </impl>
</helpset>
