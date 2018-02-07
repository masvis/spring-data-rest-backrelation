Spring Data REST Backrelation
=============================

A Java 8 library that implements the update feature for a Spring Data REST project when a POST, PATCH, PUT or DELETE method is invoked on a 
<code>mappedBy</code> <code>@ManyToMany</code> annotated field.

-------------------

Simple usage
------------
Check the **test** folder for a fast implementation of the library. 
Remember to:
* Annotate the <code>@SpringBootApplication</code> class with the <code>@EnableHandledBackrelations</code> annotation;
* Implement a <code>BackrelationHandler</code> as in <code>CompanyCityBackrelationHandler</code> test;
* Annotate a <code>@ManyToMany(mappedBy=*frontRelationField*)</code> field with a <code>@HandledBackrelation</code> 
annotation setting the correct <code>BackrelationHandler</code> bean type value.
