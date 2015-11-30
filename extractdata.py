import sys

desiredFields = {'speedX': 0, 'angle': 1, 'distFromStart': 2, 'track': 3, 'focus': 4, 'trackPos': 5, 'accel': 6, 'brake': 7, 'steer': 8}

def transformInput(line):
  if len(line) == 0:
    return None
  #else
  parsedFields = dict()
  inputs = line.strip('\x00\n').split('(')[1:]
  for field in inputs:
    splitField = field.strip().strip(')').split(' ')
    if splitField[0] in desiredFields:
      parsedFields[splitField[0]] = ','.join(splitField[1:])

  return parsedFields

def writeInOutFile(outFile, parsedFields):
  outputs = [None] * len(parsedFields)
  for key, value in parsedFields.iteritems():
    outputs[desiredFields[key]] = value
  outputString = ''
  for output in outputs:
    outputString += output + ','
  outFile.write(outputString.strip(',') + '\n')

if __name__ == '__main__':
  fileName = sys.argv[1]

  inFile = open(fileName , 'r')
  outFile = open(fileName[0:len(fileName)-4] + '.csv', 'w')

  lineNumber = 0
  parsedFields = dict()
  outFile.write(str(desiredFields) + '\n')
  for line in inFile:
    if lineNumber < 2:
      lineNumber += 1
      continue

    curParsedFields = transformInput(line)
    if not curParsedFields:
      break
    for key, value in curParsedFields.iteritems():
      if key in desiredFields and key not in parsedFields:
        parsedFields[key] = value

    if len(parsedFields) == len(desiredFields):
      writeInOutFile(outFile, parsedFields)
      parsedFields.clear()

  inFile.close()
  outFile.close()
