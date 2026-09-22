# Прога Ульяны. Нужна для построения графиков верхней оценки AoI (вычисляется здесь)
# и AoI точного (из .csv файла - результат работы моделирующей программы)

import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from scipy.stats import binom
import math

n = 100
r = 20
c = 100
p_success = 0.01

# Мат ожидание длины кадра реккурентная формула для геом распределения
def theory_formula(w):
    mu = np.zeros((n + 1, n + 1), dtype=float)

    for i in range(w - 1, -1, -1):
        n_new = n - i
        w_new = w - i
        sum_val = 0.0

        for j in range(w_new - 1, 0, -1):
            prob = binom.pmf(j, n_new, p_success)
            sum_val += prob * (1 + mu[w_new - j, n_new - j])

        for j in range(w_new, n_new + 1):
            prob = binom.pmf(j, n_new, p_success)
            sum_val += prob

        temp = binom.pmf(0, n_new, p_success)

        sum_val += temp
        sum_val /= (1 - temp)

        mu[w_new, n_new] = sum_val

    return mu[w, n]


# мат ожидание длины кадра для эксп распределения
def exp_distribution(n, w, p):
    sum = 0
    lambda_p = -math.log(1 - p)
    for i in range(n - w + 1, n + 1):
        sum += (1 / i)
    return sum / lambda_p

# вероятности чтения в определенную попытку
def probs(w):
    q = 1
    for i in range(r):
        q *= (n - w - i) / (n - i)
    p = []
    p.append(1 - q)
    while p[len(p) - 1] > 1e-03:
        p.append(p[len(p) - 1] * q)
    return p

# средний возраст инф
def aoi_res(p, mu):
    res = mu + (c - 1) / 2
    for i in range(2, len(p)):
        res += p[i - 1] * (i - 1) * (mu + c)
    return res


def main() -> None:
    res = []
    res2 = []
    w_val = []
    for w in range(1, 101):
        p_cur = probs(w)
        mu = theory_formula(w)
        mu2 = exp_distribution(n, w, p_success)
        res.append(aoi_res(p_cur, mu))
        res2.append(aoi_res(p_cur, mu2))
        w_val.append(w)

    df = pd.read_csv("results\\Params[n=100, r=20, p=0.01, c=100].csv", header=None)
    model_001 = df[0].astype(float).to_numpy()
    plt.plot(w_val, res, color="r", label='Оценка AoI', marker="+")
    # plt.plot(w_val, res2, label='test_exp', marker="+")
    plt.plot(w_val, model_001, color="k", label='Имитационное моделирование')

    plt.xlabel("w, узлов")
    plt.ylabel("Средний возраст информации, слотов")
    plt.legend()
    plt.grid(True)
    plt.show()


if __name__ == "__main__":
    main()