package helper

import (
	"github.com/ProjectLSP/internal/models"
	"github.com/golang-jwt/jwt/v5"
	"time"
)

var SingKey = []byte("AllYourBase")

type CustomClaims struct {
	ID            uint   `json:"id" form:"id"`
	Username      string `json:"username" form:"username"`
	TokoProfileID uint   `json:"toko_id" form:"toko_id"`
	Role          string `json:"role" form:"role"`
	jwt.RegisteredClaims
}

func GenerateToken(karyawan *models.Karyawan) (string, error) {
	claims := &CustomClaims{
		karyawan.ID,
		karyawan.Username,
		karyawan.TokoProfileID,
		karyawan.Role,
		jwt.RegisteredClaims{
			ExpiresAt: jwt.NewNumericDate(time.Now().Add(time.Hour * 24)),
			IssuedAt:  jwt.NewNumericDate(time.Now()),
			NotBefore: jwt.NewNumericDate(time.Now()),
			Issuer:    "Go_Api",
			Subject:   "petugas",
			ID:        "1",
			Audience:  []string{"somebody_else"},
		},
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	return token.SignedString(SingKey)
}
