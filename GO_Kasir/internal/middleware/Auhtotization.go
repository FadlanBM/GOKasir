package middleware

import (
	"strings"

	"github.com/dgrijalva/jwt-go"
	"github.com/gofiber/fiber/v2"
)

var secret = []byte("AllYourBase")

// APIKeyAuthMiddlewareAdmin Authorization godoc
// @Param Authorization header string true "Bearer {token}" "Authorization header for JWT"
func APIKeyAuthMiddlewareAdmin(c *fiber.Ctx) error {
	authorizationHeader := c.Get("Authorization")

	if authorizationHeader == "" {
		return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
			"error": "Missing or empty Authorization header",
		})
	}

	if strings.HasPrefix(authorizationHeader, "Bearer ") {
		tokenString := strings.TrimPrefix(authorizationHeader, "Bearer ")

		token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
			return secret, nil
		})

		if err != nil {
			return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
				"error": err.Error(),
			})
		}

		if claims, ok := token.Claims.(jwt.MapClaims); ok && token.Valid {
			if claims["role"].(string) != "admin" {
				return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
					"error": "Invalid Role",
				})
			}
		} else {
			return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
				"error": "Invalid Authorization header format",
			})
		}
	} else {
		return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
			"error": "Invalid Authorization header format. Use 'Bearer' schema",
		})
	}

	return c.Next()
}

// APIKeyAuthMiddleware Authorization godoc
// @Param Authorization header string true "Bearer {token}" "Authorization header for JWT"
func APIKeyAuthMiddleware(c *fiber.Ctx) error {
	authorizationHeader := c.Get("Authorization")

	if authorizationHeader == "" {
		return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
			"error": "Missing or empty Authorization header",
		})
	}

	if strings.HasPrefix(authorizationHeader, "Bearer ") {
		tokenString := strings.TrimPrefix(authorizationHeader, "Bearer ")

		token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
			return secret, nil
		})

		if err != nil {
			return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
				"error": "Invalid or expired token",
			})
		}

		if !token.Valid {
			return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
				"error": "Invalid token",
			})
		}

	} else {
		return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
			"error": "Invalid Authorization header format. Use 'Bearer' schema",
		})
	}

	return c.Next()
}

func APIKeyAuthMiddlewareMe(c *fiber.Ctx) error {
	authorizationHeader := c.Get("Authorization")

	if authorizationHeader == "" {
		return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
			"error": "Missing or empty Authorization header",
		})
	}

	if strings.HasPrefix(authorizationHeader, "Bearer ") {
		tokenString := strings.TrimPrefix(authorizationHeader, "Bearer ")

		token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
			return secret, nil
		})

		if err != nil {
			return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
				"error": "Invalid or expired token",
			})
		}

		if !token.Valid {
			return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
				"error": "Invalid token",
			})
		}

		// You can access the claims from the token using token.Claims
		// For example: claims := token.Claims.(jwt.MapClaims)
		// Save the claims to the context for later use in the controller
		c.Locals("user", token.Claims)

	} else {
		return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
			"error": "Invalid Authorization header format. Use 'Bearer' schema",
		})
	}

	return c.Next()
}
