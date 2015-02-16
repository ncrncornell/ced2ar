<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="css" scope='request'>info</c:set>
<t:main>
	<div class="infoText">
		<div class="lb">
			<h2>API Guide</h2>
			<p>
				If you are a developer and would like to incorporate our API into
				your application, please <a href="#footer">contact</a> CISER for
				more information.
			</p>
			<h4>Current URIs</h4>
			<p>The API focuses on both exposing the full codebook data in the
				underlying database, and filtering that data based on user input.
				The following endpoints are currently available:</p>
			<blockquote>
				<p>
					${baseURI}/rest/codebooks<br />
					${baseURI}/rest/codebooks/{handle} <br />
					${baseURI}/rest/codebooks/{handle}/docdesc<br />
					${baseURI}/rest/codebooks/{handle}/filedesc<br />
					${baseURI}/rest/codebooks/{handle}/studydesc<br />
					${baseURI}/rest/codebooks/{handle}/vargroups<br />
					${baseURI}/rest/codebooks/{handle}/vargroups/{vargroupID}<br />
					${baseURI}/rest/codebooks/{handle}/vargroups/{vargroupID}/vars<br />
					${baseURI}/rest/codebooks/{handle}/variables <br />
					${baseURI}/rest/codebooks/{handle}/variables/{variable name} <br />
					${baseURI}/rest/search
				</p>
			</blockquote>
		</div>
		<div class="lb">
			<h3>Accessing the API</h3>
			<p>The API is open to use - no registration required. All
				resources are read only.</p>
		</div>
		<div class="lb">
			<h3>Available Content Types</h3>
			<p>Resources are available in XML and JSON. By default, XML is
				returned.</p>
			<p>
				<em>Example</em><br />In cURL add a header to change content type:
			</p>
			<div class="code">-H &quot;Accept: application/json&quot;</div>
			<div class="code">-H &quot;Accept: application/xml&quot;</div>
			<p class="lb2">For endpoints that return variables, a minified
				representation can also be returned. Adding the header partial-text,
				and setting the value to true will strip out many of the DDI
				wrappings.</p>
			<p class="lb2">Variable resources can also be requested in CSV
				format (text/csv). CSV is only returned if partial-text is set to
				true. The format of the csv is variable name, variable label,
				codebook;
			<p />
		</div>
		<div class="lb">
			<h2>/search</h2>
			<p class="lb2">Performs a query across variables or codebooks</p>
			<h4>Required Parameter</h4>
			<p>
				<strong>return</strong><br />Specifies which category to search.
				Options are as follows:
			</p>
			<ul>
				<li>variables - variables found within all codebooks</li>
				<li>codebooks - a list of all codebooks</li>
			</ul>
			<p>
				<em>Examples</em>
			</p>
			<div class="code">${baseURI}/rest/search?return=variables</div>
			<div class="code">${baseURI}/rest/search?return=codebooks</div>
			<h4>Optional Parameters</h4>
			<div class="lb2"></div>
			<p>
				<strong>where</strong>- Provides filtering criterion. Syntax for the
				operators includes:
			</p>
			<ul class="lb2">
				<li>, and</li>
				<li>| or</li>
				<li>= equals</li>
				<li>!= does not equal</li>
				<li>&lt; less than</li>
				<li>&gt; greater than</li>
				<li>* wildcard</li>
				<li><em>art</em> contains art</li>
				<li>*art ends with art</li>
				<li>art* starts with art</li>
				<li>ar*t invalid, wildcard selector must be used at the end
					and/or beginning of phrase</li>
			</ul>
			<p class="lb2">Phrases can be compared with the syntax against a
				specific field or every field. To compare against every field, use
				the keyword allfields.</p>
			<p>For variables, the following fields can be used:</p>
			<ul>
				<li>variablename - The name of the variable as shown in its
					codebook.</li>
				<li>variablelabel - A short description of a variable</li>
				<li>variabletext - A full description of a variable</li>
				<li>variablecodeinstructions - Coder instructions</li>
				<li>variableconcept - A high level concept which the variable
					belongs to</li>
				<li>codebooktitle - The name of the codebook which the variable
					is in</li>
				<li>productdate -The date the codebook was released</li>
				<li>id -The ID attribute of a variable. May be null.</li>
			</ul>
			<p class="lb2">For codebooks only codebooktitle is valid.</p>
			<p>
				<strong>sort</strong> - Arranges results by field. A prefix of + or
				- specifies ascending or descending respectively. By default,
				sorting is in ascending order. For variables, the following fields
				can be sorted:
			</p>
			<ul>
				<li>variablename</li>
				<li>variablelabel</li>
				<li>variabletext</li>
				<li>variablecodeinstructions</li>
				<li>variableconcept</li>
				<li>codebooktitle</li>
			</ul>
			<p class="lb2">Sorting is not available for codebooks.</p>
			<p>
				<em>Example</em>
			</p>
			<div class="code">${baseURI}/rest/search?return=variables&amp;where=variablename=*abc*&amp;sort=+variablename,-variablelabel</div>
			<p>
				<strong>limit</strong> - Filters the number of results returned.
				Syntax is either a single number to specify a maximum number of
				results, or a range to specify a subset of results.
			</p>
			<p>
				<em>Example</em> - Return the first 100 variables
			</p>
			<div class="code">${baseURI}/rest/search?return=variables&amp;limit=100</div>
			<p>
				<em>Example</em> - Return variables 10 through 20.
			</p>
			<div class="code">${baseURI}/rest/search?return=variables&amp;limit=10-20</div>
			<p class="lb2">
				<em>Note the start point cannot be greater than the endpoint.
					limit=30-20 is invalid.</em>
			</p>
			<h4>Headers</h4>
			<p>When returning variables for the search endpoint, the API will
				return a counter header. This is the total number of matching
				results available without the limit parameter applied.</p>
		</div>
		<div class="lb">
			<h2>${baseURI}/rest/codebooks</h2>
			<p class="lb">
				Returns a list of all codebooks. This endpoint return CSV with
				semicolons denoting linebreaks, or space separated values. For this
				endpoint the header id-type can be used to specify how the list is
				formated. Acceptable values for this header are all, access, sn, fn
				and no value. <br /> All returns the file handle, short name and
				title in CSV. Access returns the same fields, plus the available
				access levels. SN return the shortnames, fn returns the file/handle
				names, and a null value returns the title names. Those last three
				are in space separated format.

			</p>
			<h4>${baseURI}/rest/codebooks/{handle}</h4>
			<p class="lb">Returns detailed information a on specific
				codebook, where {handle} is the name of the codebook.</p>
			<h4>${baseURI}/rest/codebooks/{handle}/docdesc</h4>
			<p class="lb">Provides detailed information in docDscr element of
				a codebook, such as production, contact and title information.</p>
			<h4>${baseURI}/rest/codebooks/{handle}/filedesc</h4>
			<p class="lb">Provides detailed information on file that the
				specific codebook is stored in.</p>
			<h4>${baseURI}/rest/codebooks/{handle}/studydesc</h4>
			<p class="lb">Provides information on a specific study</p>
			<h4>${baseURI}/rest/codebooks/{handle}/vargroups</h4>
			<p class="lb">Returns DDI varGrp classification associated with
				the codebook</p>
			<h4>${baseURI}/rest/codebooks/{handle}/vargroups/{vargroupID}</h4>
			<p class="lb">Returns specific vargroup</p>
			<h4>${baseURI}/rest/codebooks/{handle}/vargroups/{vargroupID}/vars</h4>
			<p class="lb">Returns all variables in a specific vargroup</p>
			<h4>${baseURI}/rest/codebooks/{handle}/variables</h4>
			<p class="lb">Returns a detailed list of all variables in a
				codebook.</p>
			<h4>${baseURI}/rest/codebooks/{handle}/variables/{variableId}</h4>
			<p class="lb">Returns information on a specific variable</p>
		</div>
		<div class="lb">
			<h2 class="lb2">Example Queries</h2>
			<h5>Simple Search Example 1</h5>
			<div class="code">curl -v
				${baseURI}/rest/search?return=variables&amp;where=variablename=a*&amp;sort=+variablename</div>
			<p>
				<em>Explanation</em>
			</p>
			<ul>
				<li>Return variables</li>
				<li>Where variable name starts with "a"</li>
				<li>Sort by variable name in ascending order</li>
			</ul>
		</div>
		<div class="lb">
			<h5>Simple Search Example 2</h5>
			<div class="code">curl -v
				${baseURI}/rest/search?return=variables&amp;where=allfields=war&amp;sort=+variablename</div>
			<p>
				<em>Explanation</em>
			</p>
			<ul>
				<li>Return variables</li>
				<li>And where any of the fields contains "war"</li>
				<li>Sort by variable name in ascending order</li>
			</ul>
		</div>
		<div class="lb">
			<h5>Advanced Search Example</h5>
			<div class="code">curl -v
				${baseURI}/rest/search?return=variables&amp;
				where=allfields=money,variablename=payment|variablename!=debt&amp;sort=+variablename,-codebookid&amp;limit=100</div>
			<p>
				<em>Explanation</em>
			</p>
			<ul>
				<li>Return variables</li>
				<li>Where any supported field contains war</li>
				<li>And variable name equals payment</li>
				<li>Or variable name is not equal to debt</li>
				<li>Sort by variable name in ascending order, then by codebook
					id in descending order</li>
				<li>Limit results to top 100</li>
			</ul>
		</div>
	</div>
</t:main>