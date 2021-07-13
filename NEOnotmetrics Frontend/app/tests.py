from django.test import TestCase

# Create your tests here.
# unit testing and using testcase to test functions
import unittest
from visualization import *
import json
from math import pi

from bokeh.models.tools import HoverTool, BoxZoomTool, ResetTool, SaveTool
from bokeh.plotting import figure, output_file, show

Class TestVisualization(unittest.TestCase):

  def test_Jsonread(self):
  	result=Jsonread({1:'a',2:'b',3:'c'})
  	msg="Jsonread is none."
  	self.assertIsNotNone(result,msg)

  def test_get_X(self):
  	result=get_X({1:'a',2:'b',3:'c'})
  	msg="get_X is none."
  	self.assertIsNotNone(result,msg)
  def test_get_Y(self):
  	result=get_Y({1:'a',2:'b',3:'c'})
  	msg="get_Y is none."
  	self.assertIsNotNone(result,msg)

  def test_chooseGrafik(Json,self):
  	Json={              "Axioms": 13,
                        "Logicalaxiomscount": 3,
                        "Classcount": 2
                    }
     result1= chooseGrafik(Json,"none")
     result2= "Figurtyp nicht vorhanden"
     msg= "they are not same."
     self.assertIs(result1,result2,msg)

   def test_line(self):
   	result=line({       "Axioms": 13,
                        "Logicalaxiomscount": 3,
                        "Classcount": 2
                    })
   	msg="line is none."
   	self.assertIsNotNone(result,msg)

   def test_bar(self):
   	result=bar({       "Axioms": 13,
                        "Logicalaxiomscount": 3,
                        "Classcount": 2
                    })
   	msg="bar is none."
   	self.assertIsNotNone(result,msg)

   def test_circle(self):
   	result=cirle({      "Axioms": 13,
                        "Logicalaxiomscount": 3,
                        "Classcount": 2
                    })
   	msg="cirle is none."
   	self.assertIsNotNone(result,msg)


 if __name__ == '__main__':
    unittest.main()
