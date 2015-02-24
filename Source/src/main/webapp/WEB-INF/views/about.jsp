<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="css" scope='request'>info</c:set>
<t:main>
	<div class="infoText" itemscope itemtype="http://schema.org/WebSite">
		<div class="lb">
			<h2>About the CED<sup>2</sup>AR Project</h2>
			<p itemprop="about">
				The Comprehensive Extensible Data Documentation and Access Repository (CED<sup>2</sup>AR)
				is funded by the National Science Foundation (NSF), under grant <a href="http://www.nsf.gov/awardsearch/showAward?AWD_ID=1131848" target="_blank">#1131848</a> 
				and developed by the Cornell Node of the NSF Census Research Network (NCRN).
				The Cornell NCRN branch includes researchers and developers from the Cornell Institute 
				for Social and Economic Research  <a href="http://ciser.cornell.edu/" target="_blank">(CISER)</a> 
				and the Cornell <a href="http://www.ilr.cornell.edu/ldi/" target="_blank">Labor Dynamics Institute</a>.
				CED<sup>2</sup>AR is designed to improve the discoverability of both public and restricted data from the federal statistical system.
				The project is based upon leading metadata standards and ingests data from a variety of sources.	
			</p>
		</div>
		<div class="lb">
			<h3>Supported By</h3>
			<a href='http://www.ncrn.cornell.edu/' target="_blank"><img class="imgLogo" src="images/cornell.png" alt="NRCN Cornell" width="155" height ="150"/></a>
			<a href='http://www.nsf.gov/' target="_blank"><img class="imgLogo" src="images/nsf.png" alt="NSF" width="150" height ="150"/></a>
			</div>
		<div>
			<h3>Built With</h3>
			<a href='http://getbootstrap.com/' target="_blank"><img class="imgLogo2 img-rounded" src="images/bootstrap.png" alt="Twitter Bootstrap" width="143" height ="80"/></a>
			<a href='http://www.ddialliance.org/' target="_blank"><img class="imgLogo2" src="images/ddi.png" alt="DDI" width="128" height ="80"/></a>
			<a href='http://basex.org/' target="_blank"><img class="imgLogo2" src="images/basex.png" alt="BaseX" width="80" height ="80"/></a>
		</div>
		<div class="lb">
			<h3>Current Collaborators</h3>
			<p>
				Bill Block - <span class='sh'>Cornell Institute for Social and Economic Research, Cornell University</span><br />
				Warren Brown - <span class='sh'>Cornell Institute for Social and Economic Research, Cornell University</span><br />
				Venky Kambhampaty - <span class='sh'>Cornell Institute for Social and Economic Research, Cornell University</span><br />
				Carl Lagoze - <span class='sh'>School of Information, University of Michigan</span><br />
				Benjamin Perry -  <span class='sh'>Cornell Institute for Social and Economic Research, Cornell University</span><br />
				Flavio Stanchi - <span class='sh'>Labor Dynamics Institute, Cornell University</span><br />
				Lars Vilhuber - <span class='sh'>Labor Dynamics Institute, Cornell University</span><br />
				Jeremy Williams - <span class='sh'>Cornell Institute for Social and Economic Research, Cornell University</span><br />
			</p>
		</div>
		<div class="lb">
			<h3>Previous Collaborators</h3>
			<p class="past lb">
				<span class="ph">Summer 2014</span><br />
				Kyle Brumsted - <span class='sh'>Cornell Institute for Social and Economic Research, Intern from McGill University</span><br />
			</p>
			<p class="past lb">
				<span class="ph">Fall 2013</span><br />
				Shoujun Su  - <span class='sh lb2'>Cornell University Graduate Student</span><br />
			</p>
			<p class="past lb">
				<span class="ph">Fall 2012 Class Project (Version 1.0 Beta)</span><br />
				Justin Burden - <span class='sh'>Cornell University Graduate Student</span><br />
				Chantelle Farmer - <span class='sh'>Cornell University Graduate Student</span><br />
				Jessica Kane - <span class='sh'>Cornell University Graduate Student</span><br />
				Shudan Zheng - <span class='sh'>Cornell University Graduate Student</span><br />	
			</p>
		</div>
		<div class="lb" id="legal">
			<h3>Copyright Information</h3>
			<p class="lb2">
				Copyright 2012-2015 Cornell University. All rights reserved.
				CED<sup>2</sup>AR is licensed under the Creative Commons 
				Attribution-NonCommercial-ShareAlike 4.0 International License. 
				(See <a href="http://creativecommons.org/licenses/by-nc-sa/4.0/legalcode.txt">http://creativecommons.org/licenses/by-nc-sa/4.0/legalcode.txt</a>)
				Permission to copy, modify, and distribute all or any part of CED<sup>2</sup>AR, officially docketed at Cornell as D-6801, 
				titled "The Comprehensive Extensible Data Documentation and Access Repository" ("WORK") 
				and its associated copyrights for educational, research and non-profit purposes, without fee, 
				and without a written agreement is hereby granted, provided that the above copyright notice 
				and the four paragraphs of this document appear in all copies.
			</p>
			<p class="lb2">
				Those desiring to incorporate WORK into commercial products or use WORK 
				and its associated copyrights for commercial purposes 
				should contact the Cornell Center for Technology Enterprise 
				and Commercialization at 395 Pine Tree Road, Suite 310, Ithaca, NY 14850; 
				Email:cctecconnect@cornell.edu; Tel: 607-254-4698; Fax: 607-254-5454 
				for a commercial license.
			</p>
			<p class="lb2">
				In no event shall Cornell University be liable to any party for direct, 
				indirect, special, incidental, or consequential damages, 
				including lost profits, arising out of the use of work and its associated copyrights, 
				even if Cornell University may have been advised of the possibility of such damage.
			</p>
			<p class="lb2">
				The work is provided on an "AS IS" basis, and Cornell University has no obligation 
				to provide maintenance, support, updates, enhancements, or modifications. 
				Cornell University makes no representations and extends no warranties of any kind, 
				either express or implied, including, but not limited to, the implied warranties 
				of merchantability or fitness for a particular purpose, or that the use of work 
				and its associated copyrights will not infringe any patent, trademark or other rights.
			</p>	
		</div>
		<div class="lb">
			<h3>Current Version</h3>
			<p>
				<%--TODO: Would be nice to automate this somehow --%>
				CED<sup>2</sup>AR version <span itemprop="version">2.6.0.0</span> (Updated February 24<sup>th</sup> 2015)
			</p>
			<p>
				For those interested in collaboration, please email us at
				<a class="emailContact2"></a>
			</p>
		</div>
	</div>
</t:main>