import 'package:get/get.dart';

class AnalyticController extends GetxController {
  static AnalyticController instance = Get.find();
  List listString = [].obs;
  List listData = [];
  List theSelectedOntologyFile = [].obs;
  var repositoryData;
  var repositoryName;

  String getName({required String name}) {
    if (name == 'Annotation_Assertions') {
      return 'annotationAssertionsAxioms';
    } else if (name == 'Annotation_Property_Domain_Declarations') {
      return 'annotationPropertyDomainAxioms';
    } else if (name == 'Annotation_Property_Range_Declarations') {
      return 'annotationPropertyRangeaxioms';
    } else if (name == 'Annotations_On_Classes') {
      return 'classAnnotations';
    } else if (name == 'Annotations_On_Data_Properties') {
      return 'dataPropertyAnnotations';
    } else if (name == 'Annotations_On_Datatypes') {
      return 'datatypeAnnotations';
    } else if (name == 'Annotations_On_Individuals') {
      return 'individualAnnotations';
    } else if (name == 'Annotations_On_Object_Properties') {
      return 'objectPropertyAnnotations';
    } else if (name == 'Annotations_On_Ontology') {
      return 'generalAnnotationAxioms';
    } else if (name == 'Deprecated_Classes') {
      return 'deprecatedClasses';
    } else if (name == 'Deprecated_Data_Properties') {
      return 'deprecatedDataProperties';
    } else if (name == 'Deprecated_Data_Types') {
      return 'deprecatedDataTypes';
    } else if (name == 'Deprecated_Individuals') {
      return 'deprecatedIndividuals';
    } else if (name == 'Deprecated_Object_Properties') {
      return 'deprecatedObjectProperties';
    } else if (name == 'Anonymous_Classes') {
      return 'anonymousClasses';
    } else if (name == 'Axioms') {
      return 'axioms';
    } else if (name == 'Classes') {
      return 'classes';
    } else if (name == 'Classes_That_Share_A_Relation') {
      return 'classesThatShareARelation';
    } else if (name == 'Classes_With_Instances') {
      return 'classesWithIndividuals';
    } else if (name == 'Classes_With_Multiple_Inheritance') {
      return 'classesWithMultipleInheritance';
    } else if (name == 'Classes_With_SubClasses') {
      return 'classesWithSubClasses';
    } else if (name == 'Disjoint_Classes') {
      return 'disjointClassesAxioms';
    } else if (name == 'Equivalent_Classes') {
      return 'equivalentClassesAxioms';
    } else if (name == 'Logical_Axiom_Count') {
      return 'logicalAxioms';
    } else if (name == 'Maximum_Subclasses_of_a_Class') {
      return 'maxSubClassesOfAClass';
    } else if (name == 'Maximum_Superclasses_of_a_Class') {
      return 'maxSuperClassesOfAClass';
    } else if (name == 'Sub_Class_Declarations') {
      return 'subClassOfAxioms';
    } else if (name == 'Super_Classes') {
      return 'superClasses';
    } else if (name == 'recursive_Sub_Classes') {
      return 'recursiveSubClasses';
    } else if (name == 'Maximum_Breath') {
      return 'maximalBreadth';
    } else if (name == 'Minimum_Breath') {
      return 'minimumBreath';
    } else if (name == 'Total_Breath') {
      return 'absoluteBreadth';
    } else if (name == 'Maximum_Depth') {
      return 'maximalDepth';
    } else if (name == 'Minimum_Depth') {
      return 'minimumDepth';
    } else if (name == 'Total_Depth') {
      return 'absoluteDepth';
    } else if (name == 'Leaf_Classes') {
      return 'absoluteLeafCardinality';
    } else if (name == 'max_Fanoutness_of_Leaf_Classes') {
      return 'maxFanoutnessOfLeafClasses';
    } else if (name == 'Paths_Overall') {
      return 'pathsToLeafClasses';
    } else if (name == 'Connected_Graphs') {
      return 'numberOfConnectedGraphs';
    } else if (name == 'Number_of_Cycles') {
      return 'circleHierachies';
    } else if (name == 'Class_Assertions') {
      return 'classAssertionaxioms';
    } else if (name == 'Data_Property_Assertions') {
      return 'dataPropertyAssertionAxioms';
    } else if (name == 'Different_Individuals') {
      return 'differentIndividualsAxioms';
    } else if (name == 'Individuals') {
      return 'individuals';
    } else if (name == 'Negative_Data_Property_Assertions') {
      return 'negativeDataPropertyAssertionAxioms';
    } else if (name == 'Negative_Object_Property_Assertions') {
      return 'negativeObjectPropertyAssertionAxioms';
    } else if (name == 'Object_Property_Assertions') {
      return 'objectPropertyAssertionaxioms';
    } else if (name == 'Same_Individuals') {
      return 'sameIndividualsAxioms';
    } else if (name == 'Data_Property_Domain_Declarations') {
      return 'dataPropertyDomainAxioms';
    } else if (name == 'Data_Property_Range_Declarations') {
      return 'dataPropertyRangeAxioms';
    } else if (name == 'Disjoint_Data_Properties') {
      return 'disjointDataPropertiesAxioms';
    } else if (name == 'Equivalent_Data_Properties') {
      return 'equivalentDataPropertiesAxioms';
    } else if (name == 'Functional_Data_Properties') {
      return 'functionalDataPropertyAxioms';
    } else if (name == 'Sub_Data_Properties') {
      return 'subDataPropertyOfAxioms';
    } else if (name == 'Asymmetric_Object_Properties') {
      return 'asymmetricObjectPropertyAxioms';
    } else if (name == 'Disjoint_Object_Properties') {
      return 'disjointObjectPropertyAxioms';
    } else if (name == 'Equivalent_Object_Properties') {
      return 'equivalentObjectPropertyAxioms';
    } else if (name == 'Functional_Object_Properties') {
      return 'functionalObjectPropertyAxioms';
    } else if (name == 'Inverse_Object_Properties') {
      return 'inverseObjectPropertyAxioms';
    } else if (name == 'Inversefunctional_Object_Properties') {
      return 'inverseFunctionalObjectPropertyAxioms';
    } else if (name == 'Irreflexive_Object_Properties') {
      return 'irreflexiveObjectPropertyAxioms';
    } else if (name == 'Object_Property_Domain_Declarations') {
      return 'objectPropertyDomainAxioms';
    } else if (name == 'Object_Property_Range_Declarations') {
      return 'objectPropertyRangeAxioms';
    } else if (name == 'Reflexive_Object_Properties') {
      return 'reflexiveObjectPropertyAxioms';
    } else if (name == 'Sub_Object_Properties') {
      return 'subObjectPropertyOfAxioms';
    } else if (name == 'Sub_Property_Chain_Of_Count') {
      return 'subPropertyChainOfAxioms';
    } else if (name == 'Symmetric_Object_Properties') {
      return 'symmetricObjectPropertyAxioms';
    } else if (name == 'Transitive_Object_Properties') {
      return 'transitiveObjectPropertyAxioms';
    } else if (name == 'Classes_With_Relations') {
      return 'classesWithObjectProperties';
    } else if (name == 'Data_Properties') {
      return 'dataProperties';
    } else if (name == 'Object_Properties') {
      return 'objectProperties';
    } else if (name == 'Object_Property_Class_Assertion') {
      return 'objectPropertiesOnClasses';
    } else if (name == 'Subclasses_Of_Thing') {
      return 'rootClasses';
    } else if (name == 'Super_Classes_Of_Leaf_Classes') {
      return 'superClassesOfLeafClasses';
    } else if (name == 'Super_Classes_of_Classes_With_Mulitple_Inheritance') {
      return 'superClassesOfClassesWithMultipleInheritance';
    } else if (name == 'Computational_Complexity') {
      return 'dlExpressivity';
    } else if (name == 'Consistent_Ontology') {
      return 'consistencyCheckSuccessful';
    } else if (name == 'GCI_Count') {
      return 'gciCount';
    } else if (name == 'Hidden_GCI_Gount') {
      return 'hiddengciCount';
    } else if (name == 'Inconsistent_Classes') {
      return 'inconsistentClasses';
    } else if (name == 'Reasoner_Active') {
      return 'reasonerActive';
    } else if (name == 'CohesionMetrics_ADITLN') {
      return 'CohesionMetrics_ADITLN';
    } else if (name == 'CohesionMetrics_NoL') {
      return 'CohesionMetrics_NoL';
    } else if (name == 'CohesionMetrics_NoR') {
      return 'CohesionMetrics_NoR';
    } else if (name == 'ComplexityMetrics_Average_Path_Length') {
      return 'ComplexityMetrics_Average_Path_Length';
    } else if (name == 'ComplexityMetrics_Average_Paths_Per_Concept') {
      return 'ComplexityMetrics_Average_Paths_Per_Concept';
    } else if (name == 'ComplexityMetrics_Average_Relationships_Per_Concept') {
      return 'ComplexityMetrics_Average_Relationships_Per_Concept';
    } else if (name == 'ComplexityMetrics_Max_Path_Length') {
      return 'ComplexityMetrics_Max_Path_Length';
    } else if (name == 'ComplexityMetrics_TNOC') {
      return 'ComplexityMetrics_TNOC';
    } else if (name == 'ComplexityMetrics_TNOP') {
      return 'ComplexityMetrics_TNOP';
    } else if (name == 'ComplexityMetrics_TNOR') {
      return 'ComplexityMetrics_TNOR';
    } else if (name == 'FernandezEtAl_Number_Of_Classes') {
      return 'FernandezEtAl_Number_Of_Classes';
    } else if (name == 'FernandezEtAl_Number_Of_Individuals') {
      return 'FernandezEtAl_Number_Of_Individuals';
    } else if (name == 'FernandezEtAl_Number_Of_Properties') {
      return 'FernandezEtAl_Number_Of_Properties';
    } else if (name == 'FernandezEtAl_Average_Breath') {
      return 'FernandezEtAl_Average_Breath';
    } else if (name == 'FernandezEtAl_Average_Depth') {
      return 'FernandezEtAl_Average_Depth';
    } else if (name == 'FernandezEtAl_Breath_Variance') {
      return 'FernandezEtAl_Breath_Variance';
    } else if (name == 'FernandezEtAl_Depth_Variance') {
      return 'FernandezEtAl_Depth_Variance';
    } else if (name == 'FernandezEtAl_Maximum_Breath') {
      return 'FernandezEtAl_Maximum_Breath';
    } else if (name == 'FernandezEtAl_Maximum_Depth') {
      return 'FernandezEtAl_Maximum_Depth';
    } else if (name == 'FernandezEtAl_Minimum_Breath') {
      return 'FernandezEtAl_Minimum_Breath';
    } else if (name == 'FernandezEtAl_Minimum_Depth') {
      return 'FernandezEtAl_Minimum_Depth';
    } else if (name == 'OQuaRE_ANOnto') {
      return 'OQuaRE_ANOnto';
    } else if (name == 'OQuaRE_AROnto') {
      return 'OQuaRE_AROnto';
    } else if (name == 'OQuaRE_CBOnto') {
      return 'OQuaRE_CBOnto';
    } else if (name == 'OQuaRE_CBOnto2') {
      return 'OQuaRE_CBOnto2';
    } else if (name == 'OQuaRE_CROnto') {
      return 'OQuaRE_CROnto';
    } else if (name == 'OQuaRE_DITOnto') {
      return 'OQuaRE_DITOnto';
    } else if (name == 'OQuaRE_INRonto') {
      return 'OQuaRE_INRonto';
    } else if (name == 'OQuaRE_NACOnto') {
      return 'OQuaRE_NACOnto';
    } else if (name == 'OQuaRE_NOCOnto') {
      return 'OQuaRE_NOCOnto';
    } else if (name == 'OQuaRE_NOMOnto') {
      return 'OQuaRE_NOMOnto';
    } else if (name == 'OQuaRE_POnto') {
      return 'OQuaRE_POnto';
    } else if (name == 'OQuaRE_PROnto') {
      return 'OQuaRE_PROnto';
    } else if (name == 'OQuaRE_RFCOnto') {
      return 'OQuaRE_RFCOnto';
    } else if (name == 'OQuaRE_RROnto') {
      return 'OQuaRE_RROnto';
    } else if (name == 'OQuaRE_TMOnto') {
      return 'OQuaRE_TMOnto';
    } else if (name == 'OQuaRE_TMOnto2') {
      return 'OQuaRE_TMOnto2';
    } else if (name == 'OQuaRE_WMCOnto') {
      return 'OQuaRE_WMCOnto';
    } else if (name == 'OQuaRE_WMCOnto2') {
      return 'OQuaRE_WMCOnto2';
    } else if (name == 'OntoQA_Average_Population') {
      return 'OntoQA_Average_Population';
    } else if (name == 'OntoQA_Class_Utilization') {
      return 'OntoQA_Class_Utilization';
    } else if (name == 'OntoQA_Cohesion') {
      return 'OntoQA_Cohesion';
    } else if (name == 'OntoQA_Attribute_Richness') {
      return 'OntoQA_Attribute_Richness';
    } else if (name == 'OntoQA_Class_Inheritance_Richness') {
      return 'OntoQA_Class_Inheritance_Richness';
    } else if (name == 'OntoQA_Inheritance_Richness') {
      return 'OntoQA_Inheritance_Richness';
    } else if (name == 'OntoQA_Relationship_Diversity') {
      return 'OntoQA_Relationship_Diversity';
    } else if (name == 'OntoQA_Relationship_Richness') {
      return 'OntoQA_Relationship_Richness';
    } else if (name == 'OntoQA_Schema_Deepness') {
      return 'OntoQA_Schema_Deepness';
    } else if (name == 'OQual_Absolute_Breath') {
      return 'OQual_Absolute_Breath';
    } else if (name == 'OQual_Average_Breath') {
      return 'OQual_Average_Breath';
    } else if (name == 'OQual_Maximal_Breath') {
      return 'OQual_Maximal_Breath';
    } else if (name == 'OQual_Absolute_Depth') {
      return 'OQual_Absolute_Depth';
    } else if (name == 'OQual_Average_Depth') {
      return 'OQual_Average_Depth';
    } else if (name == 'OQual_Maximal_Depth') {
      return 'OQual_Maximal_Depth';
    } else if (name == 'OQual_Absolute_Leaf_Cardinality') {
      return 'OQual_Absolute_Leaf_Cardinality';
    } else if (name == 'OQual_Absolute_Sibling_Cardinality') {
      return 'OQual_Absolute_Sibling_Cardinality';
    } else if (name == 'OQual_Average_Sibling_FanOutness') {
      return 'OQual_Average_Sibling_FanOutness';
    } else if (name == 'OQual_Maximal_Leaf_FanOutness') {
      return 'OQual_Maximal_Leaf_FanOutness';
    } else if (name == 'OQual_Maximal_Sibling_FanOutness') {
      return 'OQual_Maximal_Sibling_FanOutness';
    } else if (name == 'OQual_Ratio_of_Leaf_FanOutness') {
      return 'OQual_Ratio_of_Leaf_FanOutness';
    } else if (name == 'OQual_Ratio_of_Sibling_FanOutness') {
      return 'OQual_Ratio_of_Sibling_FanOutness';
    } else if (name == 'OQual_Weighted_Ratio_Of_Sibling_FanOutness') {
      return 'OQual_Weighted_Ratio_Of_Sibling_FanOutness';
    } else if (name == 'OQual_Weighted_Ratio_of_Leaf_FanOutness') {
      return 'OQual_Weighted_Ratio_of_Leaf_FanOutness';
    } else if (name ==
        'OQual_Ratio_of_sibling_nodes_featuring_a_shared_differentia_specifica') {
      return 'OQual_Ratio_of_sibling_nodes_featuring_a_shared_differentia_specifica';
    } else if (name == 'OQual_Anonymous_classes_ratio') {
      return 'OQual_Anonymous_classes_ratio';
    } else if (name == 'OQual_Axiomclass_ratio') {
      return 'OQual_Axiomclass_ratio';
    } else if (name == 'OQual_Class_relation_ratio') {
      return 'OQual_Class_relation_ratio';
    } else if (name == 'OQual_Generic_complexity') {
      return 'OQual_Generic_complexity';
    } else if (name == 'OQual_Inverse_relations_ratio') {
      return 'OQual_Inverse_relations_ratio';
    } else if (name == 'OQual_Modularity_rate') {
      return 'OQual_Modularity_rate';
    } else if (name == 'OrmeEtAl_Average_Fanout_of_NonLeaf_Classes_AFNL') {
      return 'OrmeEtAl_Average_Fanout_of_NonLeaf_Classes_AFNL';
    } else if (name == 'OrmeEtAl_Average_Fanout_of_Root_Class_AFR') {
      return 'OrmeEtAl_Average_Fanout_of_Root_Class_AFR';
    } else if (name == 'OrmeEtAl_Average_Fanout_per_Class_AFC') {
      return 'OrmeEtAl_Average_Fanout_per_Class_AFC';
    } else if (name == 'OrmeEtAl_Average_Properties_per_Class_APC') {
      return 'OrmeEtAl_Average_Properties_per_Class_APC';
    } else if (name == 'OrmeEtAl_Maximum_Depth_of_Inheritance_Tree_MaxDIT') {
      return 'OrmeEtAl_Maximum_Depth_of_Inheritance_Tree_MaxDIT';
    } else if (name == 'OrmeEtAl_Number_of_Properties_NOP') {
      return 'OrmeEtAl_Number_of_Properties_NOP';
    } else {
      return '';
    }
  }
}
