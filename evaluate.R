args <- commandArgs(trailingOnly = TRUE)

con<-file(args[1], open = "r")
congs<-file(args[2], open = "r")
total <- c()

while (length(resLine <- readLines(con, n = 1, warn = FALSE)) > 0 && length(gsLine <- readLines(congs, n = 1, warn = FALSE))) {
  #creates list from the file
  
  result <- (strsplit(resLine, " "))[[1]]
  gs <- (strsplit(gsLine, " "))[[1]]

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
  f3measure <- 10 * (precision * recall) / (9 * precision + recall)
  # When both recall and precision are Null we set the fmeasure as 0
  if (is.nan(fmeasure)) {
    fmeasure = 0
    f3measure = 0
  }
  m <- c(precision, recall, fmeasure, f3measure)
  total <- rbind(total, m)
}
close(con)

colnames(total) <- c("precision","recall", "fmeasure", "f3measure")
print("summary")
print(summary(total))

print("individual results")
print(total)
