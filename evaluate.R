args <- commandArgs(trailingOnly = TRUE)

con<-file(args[1], open = "r")
total <- c()

while (length(twoLines <- readLines(con, n = 2, warn = FALSE)) > 0) {
  #creates list from the file
  result <- (strsplit(twoLines[1], " "))[[1]]
  gs <- (strsplit(twoLines[2], " "))[[1]]

  # Get the list of releavant documents retrieved
  relevantDocsRetrieved <- intersect(result,gs)

  # Get the size of each necessary vector
  nbRelevantDocsRetrieved <- length(relevantDocsRetrieved)
  nbRelevantDocs <- length(gs)
  nbRetrievedDocs <- length(result)
  
  # Compute precision recall and fmeasure
  precision <- nbRelevantDocsRetrieved / nbRetrievedDocs
  recall <- nbRelevantDocsRetrieved / nbRelevantDocs
  fmeasure <- 2 * (precision * recall) / (precision + recall)
  # When both recall and precision are Null we set the fmeasure as 0
  if (is.nan(fmeasure)) {
    fmeasure = 0
  }
  m <- c(precision, recall, fmeasure)
  total <- rbind(total, m)
}
close(con)

colnames(total) <- c("precision","recall", "fmeasure")
print("summary")
print(summary(total))

print("individual results")
print(total)
