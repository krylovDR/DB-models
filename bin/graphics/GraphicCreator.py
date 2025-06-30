import matplotlib.pyplot as plt  # type: ignore
import csv
import sys

def main() -> None:

    # чтение данных из .csv файла для оси X
    x_values = []
    with open(sys.argv[1], 'r', newline='') as file_1:
        reader = csv.reader(file_1)
        for row in reader:
            x_values.append(int(row[0]))

    # чтение данных из .csv файла для оси Y
    y_values = []
    with open(sys.argv[2], 'r', newline='') as file_2:
        reader = csv.reader(file_2)
        for row in reader:
            y_values.append(float(row[0]))

    # построение графика
    fig = plt.subplots()
    plt.plot(x_values, y_values)
    plt.show()


if __name__ == "__main__":
    main()

