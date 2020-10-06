<%@page
	import="de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyUtility"%>
<%@ page import="org.semanticweb.owlapi.model.IRI"%>
<%@ page
	import="de.edu.rostock.ontologymetrics.owlapi.ontology.metric.basemetric.schemaknowledgebasemetric.classmetrics.ClassMetrics"%>
<%@ page
	import="de.edu.rostock.ontologymetrics.owlapi.ontology.OntologyMetrics"%>
<%@ page import="java.util.*"%>
<%@ page
	import="de.edu.rostock.ontologymetrics.owlapi.ontology.metric.OntologyMetric"%>
<%@ page import="java.io.*"%>
<%@ page import="java.text.DateFormat"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.text.ParseException"%>
<%@ page import="java.util.Date"%>
<%
    String hostName = OntologyUtility.getHostName();
			if (hostName.equals("onto.gg01.local")) {
				hostName = "ontometrics.informatik.uni-rostock.de";
			}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Ontology Metrics</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" href="style/result.css">
<link rel="stylesheet" href="style/main.css">
<script type="text/javascript"
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
<script type="text/javascript">
	var oldSelectedClassMetric = null;

	function hideDiv(divId) {
		$("#" + divId).hide();
		$("#hide_" + divId).hide();
		$("#show_" + divId).show();
		$("#hideLink_" + divId).hide();
		$("#showLink_" + divId).show();
	}
	function showDiv(divId) {
		$("#" + divId).show();
		$("#show_" + divId).hide();
		$("#hide_" + divId).show();
		$("#showLink_" + divId).hide();
		$("#hideLink_" + divId).show();
	}

	function hideBase(divId) {
		$("#baseMetricComplete").hide();
		$("#hide_" + divId).hide();
		$("#show_" + divId).show();
		$("#hideLink_" + divId).hide();
		$("#showLink_" + divId).show();
	}
	function showBase(divId) {
		$("#baseMetricComplete").show();
		$("#show_" + divId).hide();
		$("#hide_" + divId).show();
		$("#showLink_" + divId).hide();
		$("#hideLink_" + divId).show();
	}

	function switchClassMetrics() {
		var chbox = document.getElementById("classMetricCB");
		if (chbox.checked) {
			$("#classLabelOnly").show();
			$("#classLabelIri").hide();
		} else {
			$("#classLabelOnly").hide();
			$("#classLabelIri").show();
		}
	}

	function selectClass() {
		var selectBox = document.getElementById("selectBox");
		var selectedValue = selectBox.options[selectBox.selectedIndex].value;
		$("#class_" + selectedValue).show();
		if (oldSelectedClassMetric != null) {
			$(oldSelectedClassMetric).hide();
		}
		oldSelectedClassMetric = "#class_" + selectedValue;
	}

	function selectClass1() {
		var selectBox = document.getElementById("selectBox1");
		var selectedValue = selectBox.options[selectBox.selectedIndex].value;
		$("#class_" + selectedValue).show();
		if (oldSelectedClassMetric != null) {
			$(oldSelectedClassMetric).hide();
		}
		oldSelectedClassMetric = "#class_" + selectedValue;
	}
</script>
</head>

<body>
	<!-- begin header -->
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
				href="https://win.informatik.uni-rostock.de/index.php?id=2171"
				target="_blank">Impressum</a>
		</div>

		<div class="body_index">
			<%
			    //get parameters, attributes,...		
			    String text = request.getParameter("text");

			    String iriString = (String) request.getAttribute("classmetric");
			    Boolean isURL = (Boolean) request.getAttribute("isurl");
			    OntologyMetrics metrics = (OntologyMetrics) request.getAttribute("metrics");
			    String url = (String) request.getAttribute("src"); // is url
			    //neu
			    String messages = (String) request.getAttribute("messages"); // Meldungen
			    String errors = (String) request.getAttribute("errors"); // Fehler
			    String ontoid = (String) request.getAttribute("ontoid");

			    //neu
			    String baseMetrics = (String) request.getParameter("base");
			    String schemaMetrics = (String) request.getParameter("schema");
			    String knowledgeMetrics = (String) request.getParameter("knowledge");
			    String classMetrics = (String) request.getParameter("class");
			    String graphMetrics = (String) request.getParameter("graph");

			    System.out.println("result.jsp::ontoid: "
					    + ontoid);
			    System.out.println("result.jsp::messages: "
					    + messages);
			    System.out.println("result.jsp::errors: "
					    + errors);
			    System.out.println("result.jsp::iriString: "
					    + iriString);
			    System.out.println("result.jsp::url: "
					    + url);
			    System.out.println("result.jsp::baseMetrics: "
					    + baseMetrics);
			    System.out.println("result.jsp::schemaMetrics: "
					    + schemaMetrics);
			    System.out.println("result.jsp::knowledgeMetrics: "
					    + knowledgeMetrics);
			    System.out.println("result.jsp::classMetrics: "
					    + classMetrics);
			    System.out.println("result.jsp::graphMetrics: "
					    + graphMetrics);

			    @SuppressWarnings("unchecked")
			    List<String> namespaces = (List<String>) request.getAttribute("namespaces"); //is String
			%>
			<%
			    //out.print("<h3>If you want to download the results as xml file, please scroll down and click the \"Download-XML\" - button.</h3>");
			    out.print("<h1>Results</h1>");
			    if (ontoid != null)
					out.print("<h3>OntologyID: "
						+ ontoid
						+ "</h3>");

			    String now = (String) request.getAttribute("xmlident");
			    Integer i = 0;
			    Boolean Basemetric = false;
			    String label = null;
			    String labelTrimmed = "";
			    String labelTrimmed_before = "";
			    Object valueOfMetric;
			    Object valueOfClassMetric;

			    // print table
			    if (metrics != null) {
					// writing xml into path´
					String path = "";
					if (hostName.equals("ontometrics.informatik.uni-rostock.de")) {
					    path = "/var/www/html/tmp/"
						    + now
						    + ".xml";
					} else {
					    path = "tmp/"
						    + now
						    + ".xml";
					}
					File file = new File(File.separator + path); // i = pathname
					FileWriter writer = new FileWriter(file, false);

					// XML-Entry
					writer.write(
						"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><ontometrics-result><ontology id=\""
							+ ontoid
							+ "\">");

					for (OntologyMetric metric : metrics.getAllMetrics()) {
					    //skip calculation of class metrics if class metrics checkbox was not checked
					    if (!((metric.getLabel().equals("Class metrics") && classMetrics == null))) {
						if (!metric.getLabel().equals(label)) {
						    // String for JavaScript Hide & Show - Functions of single metric blocks
						    labelTrimmed_before = labelTrimmed;
						    labelTrimmed = metric.getLabel().replaceAll(" ", "");
						    if (i == 0) {
							// first entry doesn't require closing of a non opened table 
							i = 1;
							// XML-Entry
							writer.write("<"
								+ labelTrimmed
								+ ">");
						    } else {
							out.print("</table></div>");
							// XML-Entry
							writer.write("</"
								+ labelTrimmed_before
								+ "><"
								+ labelTrimmed
								+ ">");
						    }

						    /* #### START: Making correct Strings for different usages #### */

						    // String for Wiki Link, which uses underlines instead of white spaces and upper case for first letter of each word
						    String wikiLink = metric.getLabel();
						    wikiLink = wikiLink.replace(" a", "_A");
						    wikiLink = wikiLink.replace(" b", "_B");
						    wikiLink = wikiLink.replace(" c", "_C");
						    wikiLink = wikiLink.replace(" d", "_D");
						    wikiLink = wikiLink.replace(" e", "_E");
						    wikiLink = wikiLink.replace(" f", "_F");
						    wikiLink = wikiLink.replace(" g", "_G");
						    wikiLink = wikiLink.replace(" h", "_H");
						    wikiLink = wikiLink.replace(" i", "_I");
						    wikiLink = wikiLink.replace(" j", "_J");
						    wikiLink = wikiLink.replace(" k", "_K");
						    wikiLink = wikiLink.replace(" l", "_L");
						    wikiLink = wikiLink.replace(" m", "_M");
						    wikiLink = wikiLink.replace(" n", "_N");
						    wikiLink = wikiLink.replace(" o", "_O");
						    wikiLink = wikiLink.replace(" p", "_P");
						    wikiLink = wikiLink.replace(" q", "_Q");
						    wikiLink = wikiLink.replace(" r", "_R");
						    wikiLink = wikiLink.replace(" s", "_S");
						    wikiLink = wikiLink.replace(" t", "_T");
						    wikiLink = wikiLink.replace(" u", "_U");
						    wikiLink = wikiLink.replace(" v", "_V");
						    wikiLink = wikiLink.replace(" w", "_W");
						    wikiLink = wikiLink.replace(" x", "_X");
						    wikiLink = wikiLink.replace(" y", "_Y");
						    wikiLink = wikiLink.replace(" z", "_Z");
						    wikiLink = "https://"
							    + hostName
							    + "/wiki/index.php/"
							    + wikiLink;
						    /* #### END: Making correct Strings for different usages #### */

						    if (Basemetric == true && (metric.getLabel().equals("Schema metrics")
							    || metric.getLabel().equals("Knowledgebase metrics")
							    || metric.getLabel().equals("Class metrics")
							    || metric.getLabel().equals("Graph metrics"))) {
							Basemetric = false;
							out.print("</div>");
						    }

						    // if Class Metrics not checked - insert empty block in front of Graph Metrics
						    if ((metric.getLabel().equals("Graph metrics")) && (classMetrics == null)) {
							out.print(
								"<div class='metric_header_line'><div class='metric_header'>Class metrics &nbsp;&nbsp;&nbsp;<a class='wiki_link' href='https://"
									+ hostName
									+ "/wiki/index.php/Class_Metrics' target='_blank'>more details</a></div>");
							out.print(
								"<div class='toggleBtn'><div class='hideShowDiv' id='showLink_Classmetrics' style='display:block'><a class='showLink' id='show_Classmetrics' href='javascript:showDiv(\"Classmetrics\")'> Show </a></div><div class='hideShowDiv' id='hideLink_Classmetrics' style='display:none'><a class='hideLink' id='hide_Classmetrics' href='javascript:hideDiv(\"Classmetrics\")'> Hide </a></div></div><div class='space'></div></div>");
							out.print("<div class='metric_body' id='Classmetrics' style='display:none'>");
							out.print(
								"You did not check the class metrics checkbox, that is why they are not calculated.<br><br>");
							out.print(
								"If you want to get the results of the class metrics, please go back and check the class metric checkbox.<br>");
							out.print("</div>");
						    }

						    if (Basemetric == true) {
							out.print("<div class='metric_header_line'><div class='sub_metric_header'>"
								+ metric.getLabel()
								+ " &nbsp;&nbsp;&nbsp;<a class='wiki_link' href='"
								+ wikiLink
								+ "' target='_blank'>more details</a></div>");
						    } else {
							out.print("<div class='metric_header_line'><div class='metric_header'>"
								+ metric.getLabel()
								+ " &nbsp;&nbsp;&nbsp;<a class='wiki_link' href='"
								+ wikiLink
								+ "' target='_blank'>more details</a></div>");
						    }

						    // Button logic  -->  "hide-button" is shown when values are visible but "show-button isn't , etc.
						    if (metric.getLabel().equals("Schema metrics")) {
							if (schemaMetrics == null) {
							    out.print("<div class='toggleBtn'><div class='hideShowDiv' id='showLink_"
								    + labelTrimmed
								    + "' style='display:block'><a class='showLink' id='show_"
								    + labelTrimmed
								    + "' href='javascript:showDiv(\""
								    + labelTrimmed
								    + "\")'> Show </a></div><div class='hideShowDiv' id='hideLink_"
								    + labelTrimmed
								    + "' style='display:none'><a class='hideLink' id='hide_"
								    + labelTrimmed
								    + "' href='javascript:hideDiv(\""
								    + labelTrimmed
								    + "\")'> Hide </a></div></div><div class='space'></div></div>");
							    out.print("<div class='metric_body' id='"
								    + labelTrimmed
								    + "' style='display:none'>");
							} else if (schemaMetrics != null && schemaMetrics.equals("on")) {
							    out.print("<div class='toggleBtn'><div class='hideShowDiv' id='showLink_"
								    + labelTrimmed
								    + "' style='display:none'><a class='showLink' id='show_"
								    + labelTrimmed
								    + "' href='javascript:showDiv(\""
								    + labelTrimmed
								    + "\")'> Show </a></div><div class='hideShowDiv' id='hideLink_"
								    + labelTrimmed
								    + "' style='display:block'><a class='hideLink' id='hide_"
								    + labelTrimmed
								    + "' href='javascript:hideDiv(\""
								    + labelTrimmed
								    + "\")'> Hide </a></div></div><div class='space'></div></div>");
							    out.print("<div class='metric_body' id='"
								    + labelTrimmed
								    + "' style='display:block'>");
							}
						    } else if (metric.getLabel().equals("Knowledgebase metrics")) {
							if (knowledgeMetrics == null) {
							    out.print("<div class='toggleBtn'><div class='hideShowDiv' id='showLink_"
								    + labelTrimmed
								    + "' style='display:block'><a class='showLink' id='show_"
								    + labelTrimmed
								    + "' href='javascript:showDiv(\""
								    + labelTrimmed
								    + "\")'> Show </a></div><div class='hideShowDiv' id='hideLink_"
								    + labelTrimmed
								    + "' style='display:none'><a class='hideLink' id='hide_"
								    + labelTrimmed
								    + "' href='javascript:hideDiv(\""
								    + labelTrimmed
								    + "\")'> Hide </a></div></div><div class='space'></div></div>");
							    out.print("<div class='metric_body' id='"
								    + labelTrimmed
								    + "' style='display:none'>");
							} else if (knowledgeMetrics != null && knowledgeMetrics.equals("on")) {
							    out.print("<div class='toggleBtn'><div class='hideShowDiv' id='showLink_"
								    + labelTrimmed
								    + "' style='display:none'><a class='showLink' id='show_"
								    + labelTrimmed
								    + "' href='javascript:showDiv(\""
								    + labelTrimmed
								    + "\")'> Show </a></div><div class='hideShowDiv' id='hideLink_"
								    + labelTrimmed
								    + "' style='display:block'><a class='hideLink' id='hide_"
								    + labelTrimmed
								    + "' href='javascript:hideDiv(\""
								    + labelTrimmed
								    + "\")'> Hide </a></div></div><div class='space'></div></div>");
							    out.print("<div class='metric_body' id='"
								    + labelTrimmed
								    + "' style='display:block'>");
							}
						    } else if (metric.getLabel().equals("Class metrics")) {
							if (classMetrics == null) {
							    out.print("<div class='toggleBtn'><div class='hideShowDiv' id='showLink_"
								    + labelTrimmed
								    + "' style='display:block'><a class='showLink' id='show_"
								    + labelTrimmed
								    + "' href='javascript:showDiv(\""
								    + labelTrimmed
								    + "\")'> Show </a></div><div class='hideShowDiv' id='hideLink_"
								    + labelTrimmed
								    + "' style='display:none'><a class='hideLink' id='hide_"
								    + labelTrimmed
								    + "' href='javascript:hideDiv(\""
								    + labelTrimmed
								    + "\")'> Hide </a></div></div><div class='space'></div></div>");
							    out.print("<div class='metric_body' id='"
								    + labelTrimmed
								    + "' style='display:none'>");
							} else if (classMetrics != null && classMetrics.equals("on")) {
							    out.print("<div class='toggleBtn'><div class='hideShowDiv' id='showLink_"
								    + labelTrimmed
								    + "' style='display:none'><a class='showLink' id='show_"
								    + labelTrimmed
								    + "' href='javascript:showDiv(\""
								    + labelTrimmed
								    + "\")'> Show </a></div><div class='hideShowDiv' id='hideLink_"
								    + labelTrimmed
								    + "' style='display:block'><a class='hideLink' id='hide_"
								    + labelTrimmed
								    + "' href='javascript:hideDiv(\""
								    + labelTrimmed
								    + "\")'> Hide </a></div></div><div class='space'></div></div>");
							    out.print("<div class='metric_body' id='"
								    + labelTrimmed
								    + "' style='display:block'>");
							}
						    } else if (metric.getLabel().equals("Graph metrics")) {
							if (graphMetrics == null) {
							    out.print("<div class='toggleBtn'><div class='hideShowDiv' id='showLink_"
								    + labelTrimmed
								    + "' style='display:block'><a class='showLink' id='show_"
								    + labelTrimmed
								    + "' href='javascript:showDiv(\""
								    + labelTrimmed
								    + "\")'> Show </a></div><div class='hideShowDiv' id='hideLink_"
								    + labelTrimmed
								    + "' style='display:none'><a class='hideLink' id='hide_"
								    + labelTrimmed
								    + "' href='javascript:hideDiv(\""
								    + labelTrimmed
								    + "\")'> Hide </a></div></div><div class='space'></div></div>");
							    out.print("<div class='metric_body' id='"
								    + labelTrimmed
								    + "' style='display:none'>");
							} else if (graphMetrics != null && graphMetrics.equals("on")) {
							    out.print("<div class='toggleBtn'><div class='hideShowDiv' id='showLink_"
								    + labelTrimmed
								    + "' style='display:none'><a class='showLink' id='show_"
								    + labelTrimmed
								    + "' href='javascript:showDiv(\""
								    + labelTrimmed
								    + "\")'> Show </a></div><div class='hideShowDiv' id='hideLink_"
								    + labelTrimmed
								    + "' style='display:block'><a class='hideLink' id='hide_"
								    + labelTrimmed
								    + "' href='javascript:hideDiv(\""
								    + labelTrimmed
								    + "\")'> Hide </a></div></div><div class='space'></div></div>");
							    out.print("<div class='metric_body' id='"
								    + labelTrimmed
								    + "' style='display:block'>");
							}
						    }
						    //MPOS
						    else if (metric.getLabel().equals("Base metrics")) {
							if (baseMetrics == null) {
							    out.print("<div class='toggleBtn'><div class='hideShowDiv' id='showLink_"
								    + labelTrimmed
								    + "' style='display:block'><a class='showLink' id='show_"
								    + labelTrimmed
								    + "' href='javascript:showBase(\""
								    + labelTrimmed
								    + "\")'> Show </a></div><div class='hideShowDiv' id='hideLink_"
								    + labelTrimmed
								    + "' style='display:none'><a class='hideLink' id='hide_"
								    + labelTrimmed
								    + "' href='javascript:hideBase(\""
								    + labelTrimmed
								    + "\")'> Hide </a></div></div><div class='space'></div></div>");
							    out.print(
								    "<div class='base_metrics' id='baseMetricComplete' style='display:none'><div class='metric_body' id='"
									    + labelTrimmed
									    + "'>");
							    Basemetric = true;
							} else if (baseMetrics != null && baseMetrics.equals("on")) {
							    out.print("<div class='toggleBtn'><div class='hideShowDiv' id='showLink_"
								    + labelTrimmed
								    + "' style='display:none'><a class='showLink' id='show_"
								    + labelTrimmed
								    + "' href='javascript:showBase(\""
								    + labelTrimmed
								    + "\")'> Show </a></div><div class='hideShowDiv' id='hideLink_"
								    + labelTrimmed
								    + "' style='display:block'><a class='hideLink' id='hide_"
								    + labelTrimmed
								    + "' href='javascript:hideBase(\""
								    + labelTrimmed
								    + "\")'> Hide </a></div></div><div class='space'></div></div>");
							    out.print(
								    "<div class='base_metrics' id='baseMetricComplete' style='display:block'><div class='metric_body' id='"
									    + labelTrimmed
									    + "'>");
							    Basemetric = true;
							}
						    }
						    //MPOE
						    else {
							if (baseMetrics == null) {
							    out.print("<div class='toggleBtn'><div class='hideShowDiv' id='showLink_"
								    + labelTrimmed
								    + "' style='display:block'><a class='showLink' id='show_"
								    + labelTrimmed
								    + "' href='javascript:showDiv(\""
								    + labelTrimmed
								    + "\")'> Show </a></div><div class='hideShowDiv' id='hideLink_"
								    + labelTrimmed
								    + "' style='display:none'><a class='hideLink' id='hide_"
								    + labelTrimmed
								    + "' href='javascript:hideDiv(\""
								    + labelTrimmed
								    + "\")'> Hide </a></div></div><div class='space'></div></div>");
							    out.print("<div class='metric_body' id='"
								    + labelTrimmed
								    + "' style='display:none'>");
							} else if (baseMetrics != null && baseMetrics.equals("on")) {
							    out.print("<div class='toggleBtn'><div class='hideShowDiv' id='showLink_"
								    + labelTrimmed
								    + "' style='display:none'><a class='showLink' id='show_"
								    + labelTrimmed
								    + "' href='javascript:showDiv(\""
								    + labelTrimmed
								    + "\")'> Show </a></div><div class='hideShowDiv' id='hideLink_"
								    + labelTrimmed
								    + "' style='display:block'><a class='hideLink' id='hide_"
								    + labelTrimmed
								    + "' href='javascript:hideDiv(\""
								    + labelTrimmed
								    + "\")'> Hide </a></div></div><div class='space'></div></div>");
							    out.print("<div class='metric_body' id='"
								    + labelTrimmed
								    + "' style='display:block'>");
							}
						    }

						    out.print("<table class='metric_result' id='"
							    + labelTrimmed
							    + "'>");

						    // is class metric?
						    if (metric instanceof ClassMetrics) {
							out.print("<tr><td>");
							// apply IRI to class metric
							if (iriString != null) {
							    ClassMetrics clsMetric = (ClassMetrics) metric;
							    IRI iri = IRI.create(iriString);
							    clsMetric.setClassIRI(iri);
							    System.out.println("result.jsp::ClassMetrics: "
								    + iriString);
							}

							int showedLines = namespaces.size();
							if (showedLines > 10)
							    showedLines = 10;

							out.print(
								"<p class='hint'>To see the calculated class metric results, please click on the class you want to see in the list below.</p>");
							out.print(
								"<input class=\"classMetricCB\" type=\"checkbox\" name=\"label\" id=\"classMetricCB\" checked=\"checked\" onclick=\"javascript:switchClassMetrics()\">&nbsp;&nbsp;Class labels only<br>");

							int zaehler = 0;
							int size = namespaces.size();
							String[] JustClassName = new String[size];
							String[] IRIClassName = new String[size];

							// create list of class IRIs
							StringBuilder str = new StringBuilder();
							str.append(
								"<div class=\"classMetricArea\" id=\"classLabelOnly\" style=\"display:block\"><form action=\"ServletController\" name=\"classmetrics\"><p><select id=\"selectBox\" name=\"classmetric\" size=\""
									+ showedLines
									+ "\" onchange=\"selectClass();\">");
							for (String namespace : namespaces) {
							    // javascript functions seem not to work with special characters
							    String renamedNamespace = namespace.replace("#", "");
							    renamedNamespace = renamedNamespace.replace(".", "");
							    renamedNamespace = renamedNamespace.replace("/", "");
							    renamedNamespace = renamedNamespace.replace(":", "");
							    renamedNamespace = renamedNamespace.replace("-", "");

							    str.append("<option value=");
							    str.append(renamedNamespace); //Value
							    str.append(">");
							    str.append(namespace.substring(namespace.indexOf("#") + 1)); //Label
							    str.append("</option>");
							    JustClassName[zaehler] = namespace.substring(namespace.indexOf("#") + 1);
							    zaehler++;
							}
							str.append("</select></p></form></div>");

							// print
							out.print(str.toString());

							// SECOND BLOCK FOR DISPLAYING FULL IRI
							StringBuilder str1 = new StringBuilder();
							str1.append(
								"<div class=\"classMetricArea\" id=\"classLabelIri\" style=\"display:none\"><form action=\"ServletController\" name=\"classmetrics\"><p><select id=\"selectBox1\" name=\"classmetric\" size=\""
									+ showedLines
									+ "\" onchange=\"selectClass1();\">");

							zaehler = 0;
							for (String namespace : namespaces) {
							    // javascript functions seem not to work with special characters
							    String renamedNamespace = namespace.replace("#", "");
							    renamedNamespace = renamedNamespace.replace(".", "");
							    renamedNamespace = renamedNamespace.replace("/", "");
							    renamedNamespace = renamedNamespace.replace(":", "");
							    renamedNamespace = renamedNamespace.replace("-", "");

							    str1.append("<option value=");
							    str1.append(renamedNamespace); //Value
							    str1.append(">");
							    str1.append(namespace); //Label
							    str1.append("</option>");
							    IRIClassName[zaehler] = namespace;
							    zaehler++;
							}
							str1.append("</select></p></form></div>");

							// print
							out.print(str1.toString());

							zaehler = 0;
							// THIRD BLOCK FOR RESULTS OF EACH CLASS
							for (String IRI : namespaces) {
							    StringBuilder str2 = new StringBuilder();
							    metrics.setIRI(IRI);

							    // javascript functions seem not to work with special characters
							    String renamedIri = IRI.replace("#", "");
							    renamedIri = renamedIri.replace(".", "");
							    renamedIri = renamedIri.replace("/", "");
							    renamedIri = renamedIri.replace(":", "");
							    renamedIri = renamedIri.replace("-", "");

							    str2.append("<div class=\"classMetricContent\" id=\"class_"
								    + renamedIri
								    + "\" style=\"display: none\"><table>");
							    // XML-Entry
							    writer.write("<class iri=\""
								    + IRIClassName[zaehler]
								    + "\" name=\""
								    + JustClassName[zaehler]
								    + "\">");

							    for (OntologyMetric classMetric : metrics.getAllClassMetrics(null)) {
								valueOfClassMetric = classMetric.getValue();
								str2.append("<tr><td>"
									+ classMetric
									+ ":</td>");
								str2.append("<td>"
									+ valueOfClassMetric
									+ "</td>");
								str2.append("</tr>");// XML-Entry
								writer.write("<"
									+ OntologyUtility.CleanInvalidChars(classMetric.toString())
									+ ">"
									+ valueOfClassMetric
									+ "</"
									+ OntologyUtility.CleanInvalidChars(classMetric.toString())
									+ ">");
							    }
							    // XML-Entry
							    writer.write("</class>");
							    str2.append("</table></div>");
							    out.print(str2.toString());
							    zaehler++;
							}
							out.print("</td></tr>");
						    }

						    label = metric.getLabel();
						}
						if (!(metric instanceof ClassMetrics)) {
						    valueOfMetric = metric.getValue();
						    out.print("<tr><td>"
							    + metric
							    + ":</td>");
						    out.print("<td>"
							    + valueOfMetric
							    + "</td>");
						    out.print("</tr>");
						    // XML-Entry
						    writer.write("<"
							    + OntologyUtility.CleanInvalidChars(metric.toString())
							    + ">"
							    + valueOfMetric
							    + "</"
							    + OntologyUtility.CleanInvalidChars(metric.toString())
							    + ">");
						}
					    }

					}
					out.print("</tr></table></div>");
					if (Basemetric != true) {
					    out.print("</div>");
					}

					// XML-Entry
					writer.write("</"
						+ labelTrimmed
						+ "></ontology></ontometrics-result>");
					writer.flush();
					writer.close();
					file.createNewFile();
/* 
					if (now != null && errors.isEmpty()) {
					    OntologyUtility.writeXMLResult2DB(file, now);
					 }*/
					}

			    if (now != null && errors.isEmpty()) {
					out.print("<div class='xml_result'><h1><br>View Result as XML</h1><br>");
					out.print("<form method='GET' action='https://"
						+ hostName
						+ "/tmp/"
						+ now
						+ ".xml'  target=\"_blank\"><input type='submit' value='View XML'></form></div>");
			    }

			    //Warnungsausgabe neu
			    if (!messages.isEmpty()) {
					out.print("<div class='warnings'>");
					out.print("<h1>Warnings</h1>");
					out.print("<textarea id=\"text\" name=\"text\" rows=\"16\" cols=\"100\">"
						+ messages
						+ "</textarea>");
					out.print("</div>");
			    }
			    //Fehlerausgabe neu
			    if (!errors.isEmpty()) {
					out.print("<div class='errors'>");
					out.print("<h1>Errors</h1>");
					out.print("<textarea id=\"text\" name=\"text\" rows=\"16\" cols=\"100\">"
						+ errors
						+ "</textarea>");
					out.print("</div>");
			    }
			%>
		</div>
		<!-- end main section -->

		<!-- footer -->
		<div class="footer">
			<div class="final_links">
				Powered by the <a class="final_link"
					href="http://owlapi.sourceforge.net/">OWL API</a><br>
				Copyright by University of Rostock
			</div>
		</div>
	</div>
</body>
</html>