<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="css" scope='request'>info</c:set>
<t:main>
	<div class="infoText">
		<div class="lb">
			<h2>Frequently Asked Questions</h2>
		</div>
		<div id="q1" class="lb">
			<h4>1. Why aren't some features displayed correctly?</h4>
			<p>
				CED<sup>2</sup>AR requires a modern browser with javascript enabled.
				We do not support older versions of Internet Explorer. Regardless,
				please report any bugs or errors. (See bottom of page.)
			</p>
		</div>
		<div id="q2" class="lb">
			<h4>2. What are the search shortcuts?</h4>
			<p class="lb3">Asterisks denote a wildcard.</p>
			<ul class="lb2">
				<li>age matches terms with the root of age (age, aged, aging,
					ages)</li>
				<li>age* matches terms that starts with age (agency)</li>
				<li>*age matches terms that end with age (wage and marriage
					would match)</li>
				<li>*age* matches terms that contain age (wages would match)</li>
			</ul>
			<p class="lb2">
				A single hyphen denotes that a the variable does not contain the
				specified term. <br /> <em>Example: -age</em> <br /> Hyphens should
				be outside asterisks:
			</p>
			<ul class="lb2">
				<li>Valid: -age*</li>
				<li>Valid: -*age*</li>
				<li>Invalid: *-age*</li>
			</ul>
			<p class="lb2">Field shortcuts can be used to match a term only
				in a specific field. Append them to a beginning of a term or phrase</p>
			<ul class="lb2">
				<li>n= Variable name</li>
				<li>l= Label</li>
				<li>f= Full description</li>
				<li>c= Codebook Instructions</li>
				<li>t= Variable Concept/Type</li>
			</ul>
			<p class="lb2">
				<em>Example: n=age* </em>
			</p>
			<p class="lb3">
				Pipes denote an OR concatenation <br /> <em>Example:
					money|income</em>
			</p>
		</div>
		<div id="q3" class="lb">
			<h4>3. How do I insert ASCII math?</h4>
			<p class="lb2">
				CED<sup>2</sup>AR supports ASCII math for any editable fields. 
				The ASCII math is denoted by the grave accent (the ` symbol).
				Note this is commonly found on the same key as the diacritical tilde, 
				(the ~ symbol,) and not the apostrophe found under the double quotation mark. 
				For example the code:
			</p>
			<p class="lb2">
				<code>`B[\bar{X}_{agkt}] =\ 1/(M-1)sum_{l=1}^{100}(\hat{X}_{agkt}^{(l)} - \bar{X}_{agkt})^2`</code>
			</p>
			<p class="lb2">renders as:</p>
			<p class="lb2">
				`B[\bar{X}_{agkt}] =\ 1/(M-1)sum_{l=1}^{100}(\hat{X}_{agkt}^{(l)} - \bar{X}_{agkt})^2`
			</p>
			<p>			
				Please see <a href="http://asciimath.org/">http://asciimath.org/</a> 
				for specific syntax.
			</p>
		</div>		
	</div>
</t:main>