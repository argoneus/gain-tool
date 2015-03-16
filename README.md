# jgain
Automatically exported from code.google.com/p/gain-tool

GAIN is based on interaction information between three attributes; in this case, between two single nucleotide polymporphisms (SNPs) and a class or phenotype attribute. Interaction information is the gain in phenotype information obtained by considering SNP A and SNP B jointly beyond the phenotype information that would be gained by considering SNPs A and B independently.

Thus, each edge in a GAIN represents the increase in information about the phenotype achieved by considering the two SNPs jointly compared to the expected increase in information with the assumption of independence between the SNPs. We emphasize that a connection between SNPs in a GAIN is specific to the given phenotype because it measures the correlation between two SNPs that influences association with the phenotype. The network can be exported to Cytoscape or visualized interactively within the GAIN tool.

# Tutorial
## 1. Load and Preprocess Data

Load the small sample file provided, called "example_data.tab," is provided. In addition to tab delimited, GAIN also accepts arff format.

In the Pair-wise correlations area, select the correlation threshold (.8 by default) and click Calculate then Show Correlated tab. You may remove redundant SNPs by checking the box next to a SNP and clicking Remove. A utility is also provided in Attributes of Interest that allows the user to provide a list of SNPs to keep for analysis and remove all others. A file has been created that has a list of SNPs to keep in the data file. Choose and Apply the "correlation_filter_list.txt," which will create a smaller data set of non-redundant SNPs.
## 2. Interaction Analysis

Change the Interaction Lower Bound to .03 and Run. Decrease the lower bound to see other possible interactions not shown by a more strict threshold. In Interaction Table, click the column labeled Selected to select all SNP pairs then click Visualize Network. You can also select the Network Degrees tab to see the number of connections each node has and the Export Results tab to export results like the network to cytoscape format.
## 3. Permutation Analysis

To estimate the interaction threshold to use for the Interaction Lower Bound, click Run in Permutation Analysis. This will generate the interaction gain scores between 500 randomized pairs then return the threshold for a false connection rate of .05. The permutation algorithm looks for the randomized score such that 5% of the random scores are more extreme. This score can be interpreted as 5% of the connections above this threshold are false. 

# Requirements
Java

# References
B.A. McKinney, J.E. Crowe, Jr., J. Guo, and D. Tian, "Capturing the spectrum of interaction effects in genetic association studies by simulated evaporative cooling network analysis," 2009, PLoS Genetics 2009, 5(3): e1000432. doi:10.1371/journal.pgen.1000432. [open source](http://www.plosgenetics.org/article/info:doi/10.1371/journal.pgen.1000432)

# Acknowledgments
We would like to acknowledge the developers of [prefuse](https://github.com/prefuse/Prefuse).
