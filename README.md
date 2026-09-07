# Quantitative-Assessment-of-SysML-Models
Measuring the complexity of SysML Models
Complexity has a huge impact on systems and on the way we develop systems. It comprises features that may make things difficult to understand. Model-Based Systems Engineering (MBSE) is employing systems analysis, design and development on models of these systems, bringing together different viewpoints, with a step-by-step increase of detail. As such it replaces traditional document-centric approaches with a methodology that uses structured domain models for information exchange and system representation throughout the engineering lifecycle. Different languages exist in MBSE, each with different features, with different approaches. SysML is a frequently used language in MBSE, and many tools exist based on this language. This paper is interested in the complexity of SysML models, as it may induce useful quantitative indicators to assess and predict the complexity of systems modeled in SysML, and as such in the complexity for the development of the system later on. Two avenues are explored: objective structural metrics applied to the SysML model and assessment of the team experience. The proposed approach is implemented by prototype coded in Java. Although simpler models offer ease of comprehension and modification, they may fail to capture the full scope of system functionality. Conversely, more complex models, though richer in detail, demand higher development effort and pose challenges in maintenance and stakeholder communication. Technical and environmental factors are integrated into the complexity assessment to reflect real-world project conditions. A picture-taking drone system serves as a case study.

Unadjusted Use Case Weight (UUCW) = (Total No. of Simple Use Cases x assigned weight) + (Total number of average use cases x assigned weight) + (Total number of complex use cases x assigned weight)

Unadjusted Actor Weight (UAW) = (Total number of simple actors x assigned weight) + (Total number of average actors x assigned weight) + (Total number of complex actors x assigned weight)

TCF = 0.6 + TF/100
ECF = 1.4 + (−0.03 × EF)

UCP = (UUCW + UAW) × TCF × ECF
