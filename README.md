
Collector is a tool that allows extracting from the Open PHACTS Discovery platform series of compounds annotated with experimental data that can be used directly for building QSAR predictive models. Currently it is being used in the framework of the eTOX project.

![OPS logo ] (http://www.openphacts.org/2/images/OP_logo_300dpi.jpg)

![eTOX logo ] (http://84.89.134.131/etox-web/img/ETOX_Project_logo.png)

Collector implements a flexible collection of filters for selecting the target, the biological data ranges and the physicochemical properties of the compounds.

Collector was intended to be installed locally and implements a local database for keeping record of the data extracted. The application provides a web based user a command line user interface. 

Installation of Collector version 1.4
March 2016

Before you start
----------------
You need to have installed in your system

- Java Development Kit 7 (Oracle JDK7)
  
   You can use the jdk bundled with collector.

   Or alternatively if you don't have this software installed you can download the latest version from here:

      http://www.oracle.com/technetwork/java/javase/downloads/index.html

   The easiest way to install is to download the tgz file and extract it to a suitable place (for example /opt).


- PostgreSQL 8.4 or higher

   If you don't have this version installed you can download it from here:

      http://www.enterprisedb.com/products-services-training/pgdownload

   The easiest way is to use the graphical installer. It installs postgresql, configures it to start with the system and creates a postgres user.
   We will assume that you have PostgresSQL installed at "/opt/PostgreSQL/9.2", and a user "postgres" with a password "postgres". 
   If you have changed any of these settings please introduce the correct settings in the step 2 below.


Installation procedure
----------------------

1. Extract the contents of collector_bin.tgz to a suitable place. For example /opt/collector

2. Define the correct settings in the file environment. In particular:

	COLLECTOR_HOME=/opt/collector # the location of collector

	JAVA_HOME=$COLLECTOR_HOME/jdk # the location of JDK. We assume we will use the bundled JDK. If not modify accordingly.
	JDK_KEYSTORE_PWD=changeit # JDK default keystore value
    
	PGPASSWORD=postgres # the password of the user postgresexport
	PG_HOME=/opt/PostgreSQL/9.2/bin # Location of Postgres psql binary

4. Execute the install script

	./install

	That's all!

If the installation completed successfully you can test it executing directly collector

	./collector

You must see the following text appear in the screen:

	Collector v1.2
	Collector Commands:

	ListProtocols
	NewJob job_description target_uuid target_label protocol_id
	NewJobUniprotAccession job_description uniprot_accesion_id protocol_id
	ListJobs
	ExecuteJob job_id
	ListJobExecutions job_id
	Export job_execution_id activities|compounds sdf filename
	Export job_execution_id activities|compounds csv filename

If you have any problem with the installation, contact us at the following e-mail addresses:

   Manuel Pastor  manuel.pastor@upf.edu
   Oriol Lopez    olopez@imim.es


The source code can be obtained in http://phi.imim.es/collector

