<html>

<head>
<meta http-equiv=Content-Type content="text/html; charset=windows-1252">
<meta name=Generator content="Microsoft Word 15 (filtered)">
<title>OPI - Ontology Programming Interface</title>
<style>
<!--
 /* Font Definitions */
 @font-face
	{font-family:Courier;
	panose-1:2 7 4 9 2 2 5 2 4 4;}
@font-face
	{font-family:"Cambria Math";
	panose-1:2 4 5 3 5 4 6 3 2 4;}
 /* Style Definitions */
 p.MsoNormal, li.MsoNormal, div.MsoNormal
	{margin:0cm;
	margin-bottom:.0001pt;
	text-align:justify;
	text-indent:15.05pt;
	line-height:11.0pt;
	font-size:10.0pt;
	font-family:"Times New Roman",serif;}
p.MsoCaption, li.MsoCaption, div.MsoCaption
	{margin:0cm;
	margin-bottom:.0001pt;
	text-align:justify;
	line-height:11.0pt;
	font-size:9.0pt;
	font-family:"Times New Roman",serif;}
a:link, span.MsoHyperlink
	{color:#0563C1;
	text-decoration:underline;}
a:visited, span.MsoHyperlinkFollowed
	{color:#954F72;
	text-decoration:underline;}
p.p1a, li.p1a, div.p1a
	{mso-style-name:p1a;
	margin:0cm;
	margin-bottom:.0001pt;
	text-align:justify;
	line-height:12.0pt;
	punctuation-wrap:simple;
	text-autospace:none;
	font-size:10.0pt;
	font-family:"Times New Roman",serif;}
p.Code, li.Code, div.Code
	{mso-style-name:Code;
	mso-style-link:"Code Zchn";
	margin-top:0cm;
	margin-right:0cm;
	margin-bottom:0cm;
	margin-left:14.2pt;
	margin-bottom:.0001pt;
	text-align:justify;
	line-height:11.0pt;
	font-size:9.0pt;
	font-family:"Courier New";}
span.CodeZchn
	{mso-style-name:"Code Zchn";
	mso-style-link:Code;
	font-family:"Courier New";}
.MsoChpDefault
	{font-family:"Calibri",sans-serif;}
.MsoPapDefault
	{margin-bottom:8.0pt;
	line-height:107%;}
@page WordSection1
	{size:612.0pt 792.0pt;
	margin:70.85pt 70.85pt 2.0cm 70.85pt;}
div.WordSection1
	{page:WordSection1;}
-->
</style>

</head>

<body lang=EN-US link="#0563C1" vlink="#954F72">

<div class=WordSection1>

<p class=MsoNormal style='text-indent:0cm'><span lang=EN-GB>&nbsp;</span></p>

<p class=MsoNormal style='text-indent:0cm'><img width=621 height=184
src="Website-Descrition-Dateien/image003.png"><br clear=ALL>
</p>

<p class=MsoNormal style='text-indent:0cm'><span style='position:relative;
z-index:251661312;left:-25px;top:0px;width:589px;height:29px'><img width=589
height=29 src="Website-Descrition-Dateien/image004.png"
alt="Figure 1: Accessing the OntoMetrics API through the Firefox extension “RESTED”"></span><br
clear=ALL>
</p>

<p class=MsoNormal style='text-indent:0cm'><span lang=EN-GB>To evaluate an
ontology from a web source, one can use a get request on the endpoint </span><span
class=CodeZchn><span lang=EN-GB style='font-size:9.0pt'>http://opi.informatik.uni-rostock.de/api</span></span><span
lang=EN-GB style='font-family:Courier'> </span><span lang=EN-GB>with the
parameter </span><span lang=EN-GB style='font-family:"Courier New"'>?url</span><span
lang=EN-GB> pointing to the ontological resource. The full request should look
like the following example for a query to the friend of a friend ontology:</span></p>

<p class=MsoNormal><span lang=EN-GB>&nbsp;</span></p>

<p class=Code><span lang=EN-GB>http://opi.informatik.uni-rostock.de/api?url=http://xmlns.com/foaf/spec/20140114.rdf</span></p>

<p class=MsoNormal style='text-indent:0cm'><span lang=EN-GB style='font-family:
"Courier New"'>&nbsp;</span></p>

<p class=p1a>For assessing a local ontology that is not available at a web
resource, it is possible to use a <span style='font-family:Courier'>post</span>
request on the same endpoint (<span class=CodeZchn><span lang=EN-GB
style='font-size:9.0pt'>http://opi.informatik.uni-rostock.de/api</span></span>).
The ontology is expected in the request body. The response is printed in an XML
serialization using the same top categories and terminology used in the
GUI-version and presented in Table 2. The underlying computational engine is
the same for the API, as well as for the web-application. Like in the GUI-version,
the inclusion of class metrics significantly increases the response time and is
therefore disabled by default. If the class-metrics are required, a header key
named <span class=CodeZchn><span lang=EN-GB style='font-size:9.0pt'>classmetrics</span></span>
with the value <span class=CodeZchn><span lang=EN-GB style='font-size:9.0pt'>true</span></span>
enables the calculation.</p>

<p class=MsoNormal><span lang=EN-GB>If the target ontology is not consistent
with an RDF syntax or one of its extensions like OWL or RDF(S), the service
throws an HTTP 400 error and returns an XML consisting of further information
regarding possible causes. </span></p>

<p class=MsoNormal style='text-indent:0cm'><span lang=EN-GB>By default, all
assessed ontologies are stored internally at Rostock University for further
research purposes. This behavior can be disabled by adding the parameter </span><span
class=CodeZchn><span lang=EN-GB style='font-size:9.0pt'>save : false</span></span><span
lang=EN-GB style='font-family:Courier'> </span><span lang=EN-GB>to the header.
The response header contains the parameter </span><span class=CodeZchn><span
lang=EN-GB style='font-size:9.0pt'>saved : true</span></span><span lang=EN-GB
style='font-family:Courier'> </span><span lang=EN-GB>if the ontology is stored
on the server or</span><span lang=EN-GB style='font-family:Courier'> </span><span
class=CodeZchn><span lang=EN-GB style='font-size:9.0pt'>saved : false</span></span><span
lang=EN-GB style='font-family:Courier'> </span><span lang=EN-GB>if otherwise.</span></p>

<p class=MsoNormal><span lang=EN-GB>&nbsp;</span></p>

<p class=MsoNormal style='text-indent:0cm'><span lang=EN-GB>&nbsp;</span></p>

<p class=MsoNormal style='text-indent:0cm'><img width=224 height=160
src="Website-Descrition-Dateien/image005.jpg" align=left hspace=12></p>

<p class=MsoNormal style='text-indent:0cm'><span lang=EN-GB>&nbsp;</span></p>

<p class=MsoNormal style='text-indent:0cm'><span lang=EN-GB>If there are any
questions left, please contact:</span></p>

<p class=MsoNormal style='text-indent:0cm'><span lang=EN-GB>Achim Reiz</span></p>

<p class=MsoNormal style='text-indent:0cm'><span lang=EN-GB><a
href="mailto:achim.reiz@uni-rostock.de">achim.reiz@uni-rostock.de</a></span></p>

<p class=MsoNormal style='text-indent:0cm'><span lang=EN-GB><a
href="https://www.wirtschaftsinformatik.uni-rostock.de/lehrstuhl/team/wissenschaftliches-personal/achim-reiz/">https://www.wirtschaftsinformatik.uni-rostock.de/lehrstuhl/team/wissenschaftliches-personal/achim-reiz/</a></span></p>

<p class=MsoNormal style='text-indent:0cm'><span lang=EN-GB>&nbsp;</span></p>

</div>

</body>

</html>
