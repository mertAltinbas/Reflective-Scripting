<h1>Reflective Scripting Framework</h1>

<p>The Reflective Scripting Framework is a Java-based application designed to facilitate the execution of models and scripts in a user-friendly graphical interface. This project leverages Java reflection to dynamically load model classes and bind data from external files, allowing users to run simulations and analyze results efficiently.</p>

<h2>Key Features:</h2>
<ul>
    <li><strong>Dynamic Model Loading:</strong> The application can load model classes at runtime based on user selection, enabling flexibility in model management.</li>
    <li><strong>Data Binding:</strong> Utilizes annotations to bind data fields in model classes, allowing for seamless integration of external data files.</li>
    <li><strong>Script Execution:</strong> Users can run Groovy scripts to manipulate model data and perform calculations, with results displayed in a structured format.</li>
    <li><strong>Graphical User Interface (GUI):</strong> A user-friendly interface built with Swing, providing easy navigation and interaction with models and data.</li>
    <li><strong>TSV Results Export:</strong> Results can be exported in TSV (Tab-Separated Values) format for further analysis or reporting.</li>
</ul>

<h2>Components:</h2>
<ul>
    <li><strong>Controller Class:</strong> Manages the interaction between the model, data, and scripts. It handles data reading, model execution, and result retrieval.</li>
    <li><strong>GUI Class:</strong> Implements the graphical interface, allowing users to select models and data files, and execute models and scripts.</li>
    <li><strong>Script Editor Dialog:</strong> Provides a text area for users to create and edit Groovy scripts, with line numbering and tab support.</li>
    <li><strong>Styled List Cell Renderer:</strong> Enhances the appearance of list components in the GUI for better user experience.</li>
</ul>

<h2>Usage:</h2>
<ol>
    <li>Select a model from the list of available Java classes.</li>
    <li>Choose a data file to bind to the selected model.</li>
    <li>Run the model to execute the simulation.</li>
    <li>Optionally, create or load a Groovy script to manipulate the model's data.</li>
    <li>View and export the results in TSV format.</li>
</ol>

<h2>Requirements:</h2>
<ul>
    <li>Java Development Kit (JDK) 8 or higher</li>
    <li>Groovy (for script execution)</li>
</ul>

<h2>Installation:</h2>
<p>Clone the repository and compile the Java files. Ensure that the models and data directories are correctly set up as specified in the code.</p>
