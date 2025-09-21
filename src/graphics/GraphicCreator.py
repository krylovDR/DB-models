import matplotlib.pyplot as plt  # type: ignore
import csv
import sys

# считывание данных для построения из файла .csv
def readFile(idx : int) -> list:
    res = []
    with open(sys.argv[idx], 'r', newline='') as file:
        reader = csv.reader(file)
        for row in reader:
            res.append(float(row[0]))
    return res


def main() -> None:
    values : list = []  # 1 - id данных оси, 2 - данные
    
    # чтение данных из .csv файлов в динамический список
    for i in range(1, len(sys.argv)):
        values.append(readFile(i))

    # построение графика
    fig = plt.subplots()
    for i in range(1, len(values)):
        plt.plot(values[0], values[i])

    plt.show()


if __name__ == "__main__":
    main()

