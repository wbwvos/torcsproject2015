import sys

desiredFields = {'speedX': 0, 'angle': 1, 'distFromStart': 2, 'track': 3, 'focus': 4, 'trackPos': 5, 'speed': 6, 'trackPos': 7}

def transform(curLine, prevLine):
  outLine = prevLine[:len(curLine)-3]
  outLine.append(curLine[0]) #speed
  outLine.append(curLine[27]) #tracPos
  outFile.write(','.join(outLine) + '\n')  

if __name__ == '__main__':
  fileName = sys.argv[1]

  inFile = open(fileName , 'r')
  outFile = open(fileName[:len(fileName)-4] + 'new.csv', 'w')

  lineNumber = 0
  parsedFields = dict()
  prevLine = None
  outFile.write(str(desiredFields) + '\n')
  for line in inFile:
    if lineNumber < 1:
      lineNumber += 1
      continue

    curLine = line.strip('\n').split(',')
    if prevLine == None:
      prevLine = curLine
      continue
    
    transform(curLine, prevLine)

    prevLine = curLine

  transform(prevLine, prevLine)

  inFile.close()
  outFile.close()
