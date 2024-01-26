package controller

import (
	"github.com/dgrijalva/jwt-go"
	"github.com/gofiber/fiber/v2"
)

// MeController godoc
// @Tags Authorization
// @Summary Authenticate a user and generate a token
// @Description Authenticate a user and generate a token based on the provided credentials.
// @Accept json
// @Produce json
// @Success 200 {object} response.ResponseAuthSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/me [get]
func MeController(c *fiber.Ctx) error {
	userClaims, ok := c.Locals("user").(jwt.MapClaims)
	if !ok {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"error": "Unable to retrieve user information",
		})
	}

	userID := userClaims["id"].(float64)
	role := userClaims["role"].(string)

	response := fiber.Map{
		"user_id": userID,
		"role":    role,
	}

	return c.JSON(response)
}
