args <- commandArgs(trailingOnly = TRUE)

isRelevant <- function(doc, gs, k) {
    if (k > length(gs)) {
        k <- length(gs)
    }
    for (i in 1:k) {
        if (doc == gs[i]) {
            return(TRUE)
        }
    }
    return(FALSE)
}

calcAveP <- function(gs, result) {
    res <- 0
    for (i in 1:length(result)) {
        if (isRelevant(result[i], gs, i)) {
            precision <- length(intersect(result[1:i], gs)) / i
            res <- res + precision
        }
    }
    return(res/length(gs))
}



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

  aveP <- calcAveP(gs, result)
  # When both recall and precision are Null we set the fmeasure as 0
  if (is.nan(fmeasure)) {
    fmeasure = 0
    f3measure = 0
  }
  m <- c(precision, recall, fmeasure, f3measure, aveP)
  total <- rbind(total, m)
}
close(con)

colnames(total) <- c("precision","recall", "fmeasure", "f3measure", "averagePrecision")
print("summary")
print(summary(total))

print("individual results")
print(total)
