# Practica3

Build a Java application to search concepts and datasets in an XML catalog using SAX. The catalog contains open datasets published by Madrid’s open data portal.

Features

Input: XML catalog and corresponding XSD schema
Search: By category code
Output: New filtered XML file with only relevant concepts and datasets
Validates both input and output against XML Schemas
Implemented using:
  SAXParser from Java API
  Custom AnalizadorXML extending DefaultHandler
  GenerarXML to construct valid output from parsed data
