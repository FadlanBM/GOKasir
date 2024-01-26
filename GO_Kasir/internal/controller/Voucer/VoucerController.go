package Voucer

import (
	"errors"
	"github.com/ProjectLSP/config"
	"github.com/ProjectLSP/internal/models"
	"github.com/ProjectLSP/internal/request"
	"github.com/ProjectLSP/internal/response"
	"github.com/gofiber/fiber/v2"
	"gorm.io/gorm"
	"math/rand"
	"strconv"
	"time"
)

// Index godoc
// @Tags Crud Voucers
// @Accept json
// @Produce json
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/voucer [get]
func Index(c *fiber.Ctx) error {
	var voucer []models.Voucer

	if err := config.DB.Find(&voucer).Error; err != nil {
		return c.Status(500).JSON(fiber.Map{"Message": err.Error(), "Status": "Internal Server Error"})
	}

	var res []response.VoucerResponse

	for _, v := range voucer {
		voucerResponse := response.VoucerResponse{
			ID:        v.ID,
			Code:      v.Code,
			Discount:  strconv.FormatFloat(v.Discount, 'f', 0, 64),
			StartDate: v.StartDate.Format("02/01/2006"),
			EndDate:   v.EndDate.Format("02/01/2006"),
			IsActive:  v.IsActive,
		}
		res = append(res, voucerResponse)
	}
	return c.Status(200).JSON(fiber.Map{"Message": "Success", "Data": res})
}

// Create godoc
// @Tags Crud Voucers
// @Accept json
// @Produce json
// @Param request body request.VoucersRequest true "Request"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/voucer [post]
func Create(c *fiber.Ctx) error {
	register := new(request.VoucersRequest)

	err := c.BodyParser(register)

	if err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "error", "Message": err.Error()})
	}

	length := 8
	rand.Seed(time.Now().UnixNano())

	characters := "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

	code := make([]byte, length)
	for i := 0; i < length; i++ {
		code[i] = characters[rand.Intn(len(characters))]
	}

	tglStart, err := time.Parse("02/01/2006", register.StartDate)
	if err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "error", "Message": "Invalid date format"})
	}
	tglEnd, err := time.Parse("02/01/2006", register.EndDate)
	if err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "error", "Message": "Invalid date format"})
	}

	voucer := models.Voucer{
		Code:      string(code),
		Discount:  register.Discount,
		StartDate: tglStart,
		EndDate:   tglEnd,
		IsActive:  register.IsActive,
	}

	if err := config.DB.Create(&voucer).Error; err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "error", "Message": err.Error()})
	}
	return c.Status(200).JSON(fiber.Map{"Status": "Insert", "Message": "Successfully created", "Data": voucer})
}

// Detail godoc
// @Tags Crud Voucers
// @Accept json
// @Produce json
// @Param id path int true "Member ID"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/voucer/{id} [get]
func Detail(c *fiber.Ctx) error {
	idParam := c.Params("id")

	id, err := strconv.Atoi(idParam)
	if err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "error", "Message": err.Error()})
	}

	var voucer []models.Voucer
	if err := config.DB.First(&voucer, id).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(404).JSON(fiber.Map{"Status": "error", "Message": "Record not found"})
		}
		return c.Status(500).JSON(fiber.Map{"Status": "error", "Message": err.Error()})
	}

	var res []response.VoucerResponse

	for _, v := range voucer {
		voucerResponse := response.VoucerResponse{
			ID:        v.ID,
			Code:      v.Code,
			Discount:  strconv.FormatFloat(v.Discount, 'f', 0, 64),
			StartDate: v.StartDate.Format("02/01/2006"),
			EndDate:   v.EndDate.Format("02/01/2006"),
			IsActive:  v.IsActive,
		}
		res = append(res, voucerResponse)
	}
	return c.Status(200).JSON(fiber.Map{"Status": "Insert", "Message": "Successfully created", "Data": res})
}

// Update godoc
// @Tags Crud Voucers
// @Accept json
// @Produce json
// @Param id path int true "Member ID"
// @Param request body request.VoucersRequest true "Request"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/voucer/{id} [put]
func Update(c *fiber.Ctx) error {
	idParam := c.Params("id")

	id, err := strconv.Atoi(idParam)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"Status": "error", "Message": "Invalid ID format"})
	}

	var existingVoucer models.Voucer
	if err := config.DB.First(&existingVoucer, id).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"Status": "Error", "Message": "ID not found"})
		}
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	var requestVoucer request.VoucersRequest
	if err := c.BodyParser(&requestVoucer); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	tglStart, err := time.Parse("02/01/2006", requestVoucer.StartDate)
	if err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "error", "Message": "Invalid date format"})
	}
	tglEnd, err := time.Parse("02/01/2006", requestVoucer.EndDate)
	if err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "error", "Message": "Invalid date format"})
	}

	existingVoucer.Discount = requestVoucer.Discount
	existingVoucer.StartDate = tglStart
	existingVoucer.EndDate = tglEnd
	existingVoucer.IsActive = requestVoucer.IsActive

	if err := config.DB.Save(&existingVoucer).Error; err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	return c.Status(fiber.StatusOK).JSON(fiber.Map{"Status": "Success", "Message": "Member data updated"})
}

// Delete godoc
// @Tags Crud Voucers
// @Accept json
// @Produce json
// @Param id path int true "Member ID"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/voucer/{id} [delete]
func Delete(c *fiber.Ctx) error {
	idParam := c.Params("id")

	id, err := strconv.Atoi(idParam)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"Status": "error", "Message": "Invalid ID format"})
	}

	var Voucer models.Voucer
	if err := config.DB.First(&Voucer, id).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(404).JSON(fiber.Map{"Status": "error", "Message": "Id Petugas tidak di temukan"})
		}
		return c.Status(500).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	res := config.DB.Delete(&Voucer, id)

	if res.Error != nil {
		return c.Status(500).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	if res.RowsAffected == 0 {
		return c.Status(404).JSON(fiber.Map{"Status": "error", "Message": "Petugas Not Found"})
	}
	return c.Status(200).JSON(fiber.Map{"Status": "Success", "Message": "Data Petugas Deleted"})
}
