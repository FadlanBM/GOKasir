package controller

import (
	"github.com/ProjectLSP/config"
	"github.com/ProjectLSP/internal/helper"
	"github.com/ProjectLSP/internal/models"
	"github.com/ProjectLSP/internal/request"
	"github.com/gofiber/fiber/v2"
)

// AuthController godoc
// @Tags Authorization
// @Summary Authenticate a user and generate a token
// @Description Authenticate a user and generate a token based on the provided credentials.
// @Accept json
// @Produce json
// @Param request body request.AuthRequest true "Request"
// @Success 200 {object} response.ResponseAuthSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/auth [post]
func AuthController(c *fiber.Ctx) error {
	req := new(request.AuthRequest)

	if err := c.BodyParser(req); err != nil {
		return c.Status(400).JSON(fiber.Map{"status": "Error", "message": err.Error()})
	}

	petugas := new(models.Karyawan)
	if err := config.DB.First(petugas, "username = ?", req.Username).Error; err != nil {
		return c.Status(400).JSON(fiber.Map{"status": "Error", "message": "Account Tidak di temukan "})
	}

	if err := helper.VerivHash(petugas.Password, req.Password); err != nil {
		return c.Status(400).JSON(fiber.Map{"status": "Error", "message": "Password Salah"})
	}

	token, err := helper.GenerateToken(petugas)
	if err != nil {
		return c.Status(500).JSON(fiber.Map{"Status": "Error", "Message": "Token Generation Failed"})
	}

	return c.Status(200).JSON(fiber.Map{"token": token, "role": petugas.Role, "toko_id": petugas.TokoProfileID})
}
