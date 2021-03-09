<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*"%>
<%@page import="de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility"%>
<%
	String hostName = OntologyUtility.getHostName();
	if (hostName.equals("onto.gg01.local")) {
		hostName = "ontometrics.informatik.uni-rostock.de";
	}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>OntoMetrics</title>
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
	<!-- begin header -->
<div class="main">
	<div class="header">
	<div id="head">
		<div class="heading"><a href="./index.jsp" class="heading">OntoMetrics</a></div>
		<div class="heading_picture"><img class="heading_picture" src="img/uni_logo1.png" alt="University of Rostock" /></div>
		<div class="heading_picture"><img class="heading_picture" src="img/WINWebLogo.png" alt="Business Informatics at University of Rostock" /></div>
		
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
		|<% out.print("<a class='heading_link2' href='https://"+ hostName +"/wiki/' target=\"_blank\">Wiki</a>"); %>
		|<a class="heading_link2" href="https://win.informatik.uni-rostock.de/lehrstuhl_fuer_wirtschaftsinformatik/lehrstuhl/wissenschaftliche_mitarbeiter/dr_birger_lantow/" target="_blank">Contact</a>
		|<a class="heading_link2" href="https://www.wirtschaftsinformatik.uni-rostock.de/footer/impressum/" target="_blank">Impressum</a>
		|<a class="heading_link2" href="api.jsp" target="_blank">OntoMetrics API</a>
	</div>
	
	<div class="body_index">
		<div class="introduction">
			<p>Welcome to <b>OntoMetrics</b></p>
			
			<p>This is a web-based tool that validates and displays statistics about a given ontology.</p>
			
			<p>You can simply upload your ontology source saved as an <b>*.RDF</b> or <b>*.OWL file</b> or enter a <b>URL</b> of a document or paste <b>text</b> from your clipboard into the textarea below.</p>
		</div>
		
		<div class="fileupload">
			<form id="fileupload" name="fileupload" enctype="multipart/form-data" method="post">
				<fieldset>
					<legend>Upload a file</legend>
					<input type="file" name="fileselect" id="fileselect" onclick="resetText() "/>
					<input id="uploadbutton" onclick="upload()" type="button" value="Upload" />
				</fieldset>
			</form>
		</div>
		
		
		<div class="ontology_options">
			<form action="ServletController" method="POST">
				<div class="textinsert" id="src">
					<textarea id="text" name="text" rows="24" cols="85"></textarea>
					<textarea id="path" name="path" rows="1" cols="85" style="display:none"></textarea>
				</div>
	
				<div class="metric_selection" id="metrics">
					<!-- begin selecting metrics -->
					<!-- checkboxes to selecting metrics-->
					
						Choose the metrics, you would like to calculate.
	
					<p>
						<input type="checkbox" name="base" checked="checked">Base metrics<br>
						<input type="checkbox" name="schema">Schema metrics<br>
						<input type="checkbox" name="knowledge">Knowledgebase metrics<br>
						<input type="checkbox" name="class">Class metrics <b>*</b><br>
						<input type="checkbox" name="graph">Graph metrics
					</p>
					
						<br><br><u><b>* Note:</b></u> checking class metrics may cause long calculation times depending on the size of the ontology
				</div>
				
				<!-- end selecting metrics -->
	
				<!-- submit button -->
				<div class="submit" id="submit">
					<input type="submit" value="Calculate metrics">
				</div>
				
				<div class="store_checkbox">
					<input type="checkbox" name="store_aggreement" checked="checked">I agree that my ontology will be stored on the server for further improvements of this program.<br>
				</div>
			</form>
		</div>

		

		<script>
			function upload(evt) {
				//write filepath in dummy-item for java
				document.getElementById('path').value = document.getElementById('fileselect').value;			
			
				//Retrieve the first (and only!) File from the FileList object
				var input = document.getElementById('fileselect');
				var f = input.files[0];
				if (f) {
					var r = new FileReader();
					r.onload = function(e) {
						var contents = e.target.result;
						document.getElementById('text').value = contents;
					}
					r.readAsText(f);
					
				} else {
					alert("Failed to load file");
				}
			}

			document.getElementById('uploadbutton').addEventListener('onclick',
					upload, false);
		</script>


	</div>
	<!-- end main section -->

	<!-- footer -->
	<div class="footer">
		<div class="final_links">
			Powered by the <a class="final_link" href="http://owlapi.sourceforge.net/">OWL API</a><br>
			Copyright by University of Rostock
		</div>
	</div>
</div>	
</body>
</html>