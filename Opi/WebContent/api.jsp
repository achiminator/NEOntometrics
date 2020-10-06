<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*"%>
<%@page
	import="de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility"%>
<%
    String hostName = OntologyUtility.getHostName();
			if (hostName.equals("onto.gg01.local")) {
				hostName = "ontometrics.informatik.uni-rostock.de";
			}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>OntoMetrics API</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" href="style/main.css">
<link rel="stylesheet" href="style/result.css">
<script type="text/javascript">
	function resetText() {
		document.getElementById("text").value = "";
	}
</script>
</head>
<body>
	<div class="main">
		<div class="header">
			<div id="head">
				<div class="heading">
					<a href="./index.jsp" class="heading">OntoMetrics</a>
				</div>
				<div class="heading_picture">
					<img class="heading_picture" src="img/uni_logo1.png"
						alt="University of Rostock" />
				</div>
				<div class="heading_picture">
					<img class="heading_picture" src="img/WINWebLogo.png"
						alt="Business Informatics at University of Rostock" />
				</div>

				<!-- <div class="heading2">
			<a class="heading_link1" href="#">Home</a>
			|<a class="heading_link2" href="#">Result</a> 
			|<a class="heading_link2" href="#">FAQ</a>
		</div> -->
			</div>
		</div>

		<!-- end header -->
		<!-- begin main section -->
		<div class="body_top"></div>
		<div class="link_section">
			<a class="heading_link1" href="./index.jsp">Home</a>
			<!-- |<a class="heading_link2" href="#">Result</a>
		|<a class="heading_link2" href="#">FAQ</a> -->
			|<%
			    out.print("<a class='heading_link2' href='https://"
					    + hostName
					    + "/wiki/' target=\"_blank\">Wiki</a>");
			%>
			|<a class="heading_link2"
				href="https://win.informatik.uni-rostock.de/lehrstuhl_fuer_wirtschaftsinformatik/lehrstuhl/wissenschaftliche_mitarbeiter/dr_birger_lantow/"
				target="_blank">Contact</a> |<a class="heading_link2"
				href="https://www.wirtschaftsinformatik.uni-rostock.de/footer/impressum/"
				target="_blank">Impressum</a> |<a class="heading_link2"
				href="api.jsp" >OntoMetrics API</a>
		</div>

		<div class="body_index">
			<div class="introduction">
				<h2>
					OntoMetrics <b>API</b>
				</h2>
				<p>The OntoMetrics API is accessible at the Endpoint /api
				<br />Using a Get request with the parameter "url", an ontology web source can be accessed and analyzed <i>(e.g. opi.informatik.uni-rostock.de/api?url=http://xmlns.com/foaf/spec/20140114.rdf)</i>
				<br /><br />Further, it is possible to attach an ontology in a POST-Request. Simply use the endpoint of the selected version of the API and attach the ontology in the body of the request, without any more parameters.
				</p>
				<p>
				for questions please contact <a
					href="https://www.wirtschaftsinformatik.uni-rostock.de/lehrstuhl/team/wissenschaftliches-personal/achim-reiz/">Achim
					Reiz </a>


				</p>
			</div>
</body>
</html>